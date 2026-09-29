package io.github.linkszf.fastcall.core.declarative;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.springframework.web.multipart.MultipartFile;
import io.github.linkszf.fastcall.common.FcMediaType;
import io.github.linkszf.fastcall.common.FcRequestMethod;
import io.github.linkszf.fastcall.common.exception.FastCallException;
import io.github.linkszf.fastcall.common.json.FcJsonCodec;
import io.github.linkszf.fastcall.common.model.FcApiPak;
import io.github.linkszf.fastcall.common.model.FcApiParamPak;
import io.github.linkszf.fastcall.common.model.FcSourcePak;
import io.github.linkszf.fastcall.common.source.IFcSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
final class FcClientRequestResolver {

    private static final Pattern PATH_PLACEHOLDER_PATTERN = Pattern.compile("\\{([^{}]+)}");

    private final IFcSource source;

    private final FcJsonCodec jsonCodec;

    FcResolvedRequest resolve(FcClientMethodMetadata metadata, Object[] args) {
        String system = metadata.getSystem();
        String resolvedHost = metadata.getHost();
        String resolvedUri = metadata.getUri();
        FcRequestMethod method = metadata.getRequestMethod();
        FcMediaType bodyType = metadata.getDefaultBodyType();

        Map<String, List<String>> queries = new LinkedHashMap<>();
        Map<String, List<String>> headers = new LinkedHashMap<>();

        Object body = null;
        boolean hasBody = false;
        boolean hasPart = false;
        List<PartValue> partValues = new ArrayList<>();

        String apiName = metadata.getApiName();
        if (StrUtil.isNotBlank(apiName)) {
            FcApiPak api = getApi(system, apiName);
            resolvedUri = api.getPath();
            method = Optional.ofNullable(api.getMethod()).orElse(method);
            resolvedHost = StrUtil.isNotBlank(metadata.getHost()) ? metadata.getHost() : api.getParticularHost();

            FcApiParamPak defaultParams = api.getParamPak();
            if (Objects.nonNull(defaultParams)) {
                putSingleValueMap(queries, defaultParams.getParams());
                putSingleValueMap(headers, defaultParams.getHeaders());
                if (Objects.nonNull(defaultParams.getBody())) {
                    body = defaultParams.getBody();
                    hasBody = true;
                }
            }
        }

        List<FcClientMethodMetadata.ParamBinding> bindings = metadata.getParamBindings();
        Object[] safeArgs = (Objects.nonNull(args)) ? args : new Object[0];
        for (FcClientMethodMetadata.ParamBinding binding : bindings) {
            Object arg = safeArgs[binding.getIndex()];
            if (Objects.isNull(arg)) {
                continue;
            }

            switch (binding.getKind()) {
                case QUERY:
                    putKvArg(queries, binding, arg, "query");
                    break;
                case HEADER:
                    putKvArg(headers, binding, arg, "header");
                    break;
                case PATH:
                    String placeholder = "{" + binding.getName() + "}";
                    if (!StrUtil.contains(resolvedUri, placeholder)) {
                        throw new FastCallException("Path variable [{}] does not exist in URI template [{}]", binding.getName(), resolvedUri);
                    }
                    resolvedUri = StrUtil.replace(resolvedUri, placeholder, URLUtil.encode(String.valueOf(arg)));
                    break;
                case BODY:
                    body = arg;
                    hasBody = true;
                    bodyType = binding.getBodyMediaType();
                    break;
                case PART:
                    hasPart = true;
                    addPartValues(partValues, binding, arg);
                    break;
                case SYSTEM:
                    system = String.valueOf(arg);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported param kind: " + binding.getKind());
            }
        }

        if (hasPart) {
            body = buildMultipartBody(partValues);
            hasBody = true;
            bodyType = FcMediaType.MULTIPART_FORM_DATA;
        } else if (hasBody && bodyType == FcMediaType.APPLICATION_FORM_URLENCODED) {
            body = convertToFormBody(body);
        }

        validateResolvedUriTemplate(resolvedUri);
        return new FcResolvedRequest(system, method, resolvedHost, resolvedUri, headers, queries, body, hasBody, bodyType);
    }

    private void putSingleValueMap(Map<String, List<String>> target, Map<String, String> source) {
        if (CollectionUtil.isEmpty(source)) {
            return;
        }
        source.forEach((k, v) -> {
            if (Objects.nonNull(k) && Objects.nonNull(v)) {
                addValue(target, k, v);
            }
        });
    }

    private void putKvArg(
            Map<String, List<String>> target,
            FcClientMethodMetadata.ParamBinding binding,
            Object arg,
            String type
    ) {
        if (binding.isExpandEntries()) {
            if (arg instanceof Map) {
                ((Map<?, ?>) arg).forEach((k, v) -> {
                    if (Objects.nonNull(k) && Objects.nonNull(v)) {
                        putValue(target, String.valueOf(k), v);
                    }
                });
                return;
            }

            Map<String, Object> beanMap = jsonCodec.toPropertyMap(arg);
            if (Objects.isNull(beanMap)) {
                // This type serializes to a scalar (for example when @JsonValue is declared on the type), so it cannot be expanded into multiple key-value pairs
                String expandKey = StrUtil.trim(binding.getName());
                if (StrUtil.isBlank(expandKey)) {
                    throw new FastCallException(
                            "Parameter[{}] of type[{}] is serialized as a single value, an explicit key is required for the {} parameter",
                            binding.getIndex(),
                            arg.getClass().getName(),
                            type
                    );
                }
                putValue(target, expandKey, jsonCodec.toScalar(arg));
                return;
            }

            beanMap.forEach((k, v) -> {
                if (Objects.nonNull(k) && Objects.nonNull(v)) {
                    putValue(target, k, v);
                }
            });
            return;
        }

        String key = binding.getName();
        if (StrUtil.isBlank(key)) {
            throw new FastCallException("Blank {} key is not allowed for single-value parameter", type);
        }
        putValue(target, key, arg);
    }

    private void putValue(Map<String, List<String>> target, String key, Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        if (isIterableValue(value)) {
            foreachValue(value, item -> {
                if (Objects.nonNull(item)) {
                    addValue(target, key, String.valueOf(item));
                }
            });
            return;
        }
        addValue(target, key, String.valueOf(value));
    }

    private void addValue(Map<String, List<String>> target, String key, String value) {
        List<String> values = target.computeIfAbsent(key, k -> new ArrayList<>());
        values.add(value);
    }

    private void addPartValues(List<PartValue> parts, FcClientMethodMetadata.ParamBinding binding, Object arg) {
        if (isIterableValue(arg) && !(arg instanceof byte[])) {
            foreachValue(arg, item -> {
                if (Objects.nonNull(item)) {
                    parts.add(new PartValue(binding, item));
                }
            });
            return;
        }
        parts.add(new PartValue(binding, arg));
    }

    private RequestBody convertToFormBody(Object body) {
        if (!(body instanceof Map)) {
            throw new FastCallException(
                    "@FcBody with mediaType={} requires argument type Map",
                    FcMediaType.APPLICATION_FORM_URLENCODED.getName()
            );
        }
        Map<?, ?> map = (Map<?, ?>) body;
        FormBody.Builder builder = new FormBody.Builder(StandardCharsets.UTF_8);
        map.forEach((k, v) -> {
            if (Objects.isNull(k) || Objects.isNull(v)) {
                return;
            }
            if (isIterableValue(v)) {
                foreachValue(v, item -> {
                    if (Objects.nonNull(item)) {
                        builder.add(String.valueOf(k), String.valueOf(item));
                    }
                });
            } else {
                builder.add(String.valueOf(k), String.valueOf(v));
            }
        });
        return builder.build();
    }

    private RequestBody buildMultipartBody(List<PartValue> partValues) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (PartValue partValue : partValues) {
            addMultipartPart(builder, partValue);
        }
        return builder.build();
    }

    private void addMultipartPart(MultipartBody.Builder builder, PartValue partValue) {
        FcClientMethodMetadata.ParamBinding binding = partValue.binding;
        Object value = partValue.value;
        String partName = binding.getName();
        String fileName = binding.getPartFileName();
        FcMediaType partType = Optional.ofNullable(binding.getPartMediaType()).orElse(FcMediaType.APPLICATION_OCTET_STREAM);

        if (value instanceof MultipartFile) {
            MultipartFile multipartFile = (MultipartFile) value;
            builder.addFormDataPart(
                    partName,
                    resolveMultipartFileName(fileName, multipartFile),
                    RequestBody.create(
                            readMultipartFileBytes(multipartFile),
                            resolveMultipartFileMediaType(binding.getPartMediaType(), multipartFile)
                    )
            );
            return;
        }
        if (value instanceof File) {
            File file = (File) value;
            String finalFileName = StrUtil.blankToDefault(fileName, file.getName());
            builder.addFormDataPart(partName, finalFileName, RequestBody.create(file, MediaType.parse(partType.getName())));
            return;
        }
        if (value instanceof byte[]) {
            String finalFileName = StrUtil.blankToDefault(fileName, partName);
            builder.addFormDataPart(partName, finalFileName, RequestBody.create((byte[]) value, MediaType.parse(partType.getName())));
            return;
        }
        if (value instanceof InputStream) {
            String finalFileName = StrUtil.blankToDefault(fileName, partName);
            builder.addFormDataPart(
                    partName,
                    finalFileName,
                    createStreamingRequestBody((InputStream) value, MediaType.parse(partType.getName()))
            );
            return;
        }
        if (value instanceof RequestBody) {
            String finalFileName = StrUtil.blankToDefault(fileName, partName);
            builder.addFormDataPart(partName, finalFileName, (RequestBody) value);
            return;
        }
        builder.addFormDataPart(partName, String.valueOf(value));
    }

    /**
     * The file name comes from the {@code fileName} declared on the annotation, then from the file's own original file name, and finally falls back to the form field name.
     */
    private String resolveMultipartFileName(String declaredFileName, MultipartFile multipartFile) {
        String finalFileName = StrUtil.blankToDefault(StrUtil.trim(declaredFileName), multipartFile.getOriginalFilename());
        return StrUtil.blankToDefault(StrUtil.trim(finalFileName), multipartFile.getName());
    }

    /**
     * Part content type: when the annotation declares it explicitly (not the {@code APPLICATION_OCTET_STREAM} default), the annotation wins,
     * otherwise the type declared by the file itself is used, and finally it falls back to {@code application/octet-stream}.
     */
    private MediaType resolveMultipartFileMediaType(FcMediaType declaredPartType, MultipartFile multipartFile) {
        if (Objects.nonNull(declaredPartType) && declaredPartType != FcMediaType.APPLICATION_OCTET_STREAM) {
            return MediaType.parse(declaredPartType.getName());
        }

        MediaType fileMediaType = MediaType.parse(StrUtil.trimToEmpty(multipartFile.getContentType()));
        if (Objects.nonNull(fileMediaType)) {
            return fileMediaType;
        }
        return MediaType.parse(FcMediaType.APPLICATION_OCTET_STREAM.getName());
    }

    private byte[] readMultipartFileBytes(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new FastCallException(
                    e,
                    "Failed to read bytes of multipart file[{}]",
                    multipartFile.getOriginalFilename()
            );
        }
    }

    private RequestBody createStreamingRequestBody(InputStream inputStream, MediaType mediaType) {
        return new RequestBody() {
            private volatile byte[] cachedPayload;

            private volatile boolean loaded;

            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                return loaded ? cachedPayload.length : -1L;
            }

            @Override
            public void writeTo(BufferedSink sink) {
                byte[] payload = loadPayloadOnce();
                try {
                    sink.write(payload);
                } catch (IOException e) {
                    throw new FastCallException(e, "Failed to write cached multipart input stream body");
                }
            }

            private byte[] loadPayloadOnce() {
                if (loaded) {
                    return cachedPayload;
                }

                synchronized (this) {
                    if (loaded) {
                        return cachedPayload;
                    }
                    cachedPayload = readAllBytes(inputStream);
                    loaded = true;
                    return cachedPayload;
                }
            }
        };
    }

    private static byte[] readAllBytes(InputStream inputStream) {
        try (InputStream in = inputStream;
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[8192];
            int len;
            while ((len = in.read(chunk)) != -1) {
                buffer.write(chunk, 0, len);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new FastCallException(e, "Failed to stream multipart input stream body");
        }
    }

    private boolean isIterableValue(Object value) {
        return value instanceof Iterable || value.getClass().isArray();
    }

    private void foreachValue(Object iterableOrArray, ValueConsumer consumer) {
        if (iterableOrArray instanceof Iterable) {
            for (Object item : (Iterable<?>) iterableOrArray) {
                consumer.accept(item);
            }
            return;
        }

        int length = Array.getLength(iterableOrArray);
        for (int i = 0; i < length; i++) {
            consumer.accept(Array.get(iterableOrArray, i));
        }
    }

    private void validateResolvedUriTemplate(String resolvedUri) {
        int leftBraceCount = StrUtil.count(resolvedUri, "{");
        int rightBraceCount = StrUtil.count(resolvedUri, "}");
        if (leftBraceCount == 0 && rightBraceCount == 0) {
            return;
        }

        if (leftBraceCount != rightBraceCount) {
            throw new FastCallException("URI template contains unbalanced braces: {}", resolvedUri);
        }

        Matcher matcher = PATH_PLACEHOLDER_PATTERN.matcher(resolvedUri);
        int placeholderCount = 0;
        while (matcher.find()) {
            placeholderCount++;
        }

        if (placeholderCount != leftBraceCount) {
            throw new FastCallException("URI template contains malformed path variable segment: {}", resolvedUri);
        }

        throw new FastCallException("URI template contains unresolved path variables: {}", resolvedUri);
    }

    private FcApiPak getApi(String system, String apiName) {
        FcSourcePak sourcePak = Optional.ofNullable(source.getSourcePak(system))
                .orElseThrow(() -> new FastCallException("Source infos of system[{}] do not exist", system));

        return Optional.ofNullable(sourcePak.getApiMap())
                .map(apiMap -> apiMap.get(apiName))
                .orElseThrow(() -> new FastCallException("Source infos of api[{}] in system[{}] do not exist", apiName, system));
    }

    @RequiredArgsConstructor
    private static final class PartValue {
        private final FcClientMethodMetadata.ParamBinding binding;
        private final Object value;
    }

    private interface ValueConsumer {
        void accept(Object value);
    }
}

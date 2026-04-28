package priv.szf.fastcall.core.declarative;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.FcApiPak;
import priv.szf.fastcall.common.model.FcApiParamPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.source.IFcSource;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
final class FcClientRequestResolver {

    private static final Pattern PATH_PLACEHOLDER_PATTERN = Pattern.compile("\\{([^{}]+)}");

    private static final ConcurrentMap<Class<?>, List<PropertyDescriptor>> BEAN_PROPERTY_CACHE = new ConcurrentHashMap<>();

    private final IFcSource source;

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

            FcApiParamPak defaultParams = api.getParams();
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

            Map<String, Object> beanMap = toBeanPropertyMap(arg);
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

    private Map<String, Object> toBeanPropertyMap(Object bean) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            for (PropertyDescriptor descriptor : getBeanPropertyDescriptors(bean.getClass())) {
                Method readMethod = descriptor.getReadMethod();
                if (Objects.isNull(readMethod)) {
                    continue;
                }
                if (!readMethod.isAccessible()) {
                    readMethod.setAccessible(true);
                }
                Object value = readMethod.invoke(bean);
                if (Objects.nonNull(value)) {
                    result.put(descriptor.getName(), value);
                }
            }
            return result;
        } catch (Exception e) {
            throw new FastCallException("Failed to expand bean parameter [{}]: {}", bean.getClass().getName(), e.getMessage());
        }
    }

    private List<PropertyDescriptor> getBeanPropertyDescriptors(Class<?> beanClass) {
        return BEAN_PROPERTY_CACHE.computeIfAbsent(beanClass, this::loadBeanPropertyDescriptors);
    }

    private List<PropertyDescriptor> loadBeanPropertyDescriptors(Class<?> beanClass) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, Object.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (propertyDescriptors == null || propertyDescriptors.length == 0) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(Arrays.asList(propertyDescriptors));
        } catch (Exception e) {
            throw new FastCallException("Failed to inspect bean parameter [{}]: {}", beanClass.getName(), e.getMessage());
        }
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

    private RequestBody createStreamingRequestBody(InputStream inputStream, MediaType mediaType) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                return -1L;
            }

            @Override
            public void writeTo(BufferedSink sink) {
                try (Source source = Okio.source(inputStream)) {
                    sink.writeAll(source);
                } catch (Exception e) {
                    throw new FastCallException(e, "Failed to stream multipart input stream body");
                }
            }
        };
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

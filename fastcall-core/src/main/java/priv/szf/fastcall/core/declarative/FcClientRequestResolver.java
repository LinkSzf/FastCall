package priv.szf.fastcall.core.declarative;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.RequiredArgsConstructor;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.FcApiPak;
import priv.szf.fastcall.common.model.FcApiParamPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.source.IFcSource;

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

    FcResolvedRequest resolve(FcClientMethodMetadata metadata, Object[] args) {
        String system = metadata.getSystem();
        String resolvedHost = metadata.getHost();
        String resolvedUri = metadata.getUri();
        FcRequestMethod method = metadata.getRequestMethod();
        FcMediaType bodyType = metadata.getDefaultBodyType();

        Map<String, String> queries = new LinkedHashMap<>();
        Map<String, String> headers = new LinkedHashMap<>();
        Object body = null;
        boolean hasBody = false;

        String apiName = metadata.getApiName();
        if (StrUtil.isNotBlank(apiName)) {
            FcApiPak api = getApi(system, apiName);
            resolvedUri = api.getPath();
            method = Optional.ofNullable(api.getMethod()).orElse(method);
            resolvedHost = StrUtil.isNotBlank(metadata.getHost()) ? metadata.getHost() : api.getParticularHost();

            FcApiParamPak defaultParams = api.getParams();
            if (Objects.nonNull(defaultParams)) {
                if (CollectionUtil.isNotEmpty(defaultParams.getParams())) {
                    queries.putAll(defaultParams.getParams());
                }
                if (CollectionUtil.isNotEmpty(defaultParams.getHeaders())) {
                    headers.putAll(defaultParams.getHeaders());
                }
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
                    putKvArg(queries, binding.getName(), arg, "query");
                    break;
                case HEADER:
                    putKvArg(headers, binding.getName(), arg, "header");
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
                default:
                    throw new UnsupportedOperationException("Unsupported param kind: " + binding.getKind());
            }
        }

        validateResolvedUriTemplate(resolvedUri);

        return new FcResolvedRequest(system, method, resolvedHost, resolvedUri, headers, queries, body, hasBody, bodyType);
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

    private void putKvArg(Map<String, String> target, String key, Object arg, String type) {
        if (arg instanceof Map) {
            if (StrUtil.isNotBlank(key)) {
                throw new FastCallException("{} parameter is Map, so annotation key must be blank", type);
            }
            ((Map<?, ?>) arg).forEach((k, v) -> {
                if (Objects.nonNull(k) && Objects.nonNull(v)) {
                    target.put(String.valueOf(k), String.valueOf(v));
                }
            });
            return;
        }

        if (StrUtil.isBlank(key)) {
            throw new FastCallException("Blank {} key is only supported when argument type is Map", type);
        }
        target.put(key, String.valueOf(arg));
    }

    private FcApiPak getApi(String system, String apiName) {
        FcSourcePak sourcePak = Optional.ofNullable(source.getSourcePak(system))
                .orElseThrow(() -> new FastCallException("Source infos of system[{}] do not exist", system));

        return Optional.ofNullable(sourcePak.getApiMap())
                .map(apiMap -> apiMap.get(apiName))
                .orElseThrow(() -> new FastCallException("Source infos of api[{}] in system[{}] do not exist", apiName, system));
    }
}

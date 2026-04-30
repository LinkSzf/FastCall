package priv.szf.fastcall.core.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class FcRequestBuildSupport {

    private FcRequestBuildSupport() {
    }

    public static Map<String, List<String>> toMultiValueParams(Map<String, String> params) {
        Map<String, List<String>> normalized = new LinkedHashMap<>();
        if (CollectionUtil.isEmpty(params)) {
            return normalized;
        }
        params.forEach((k, v) -> {
            if (Objects.nonNull(k) && Objects.nonNull(v)) {
                addMultiValue(normalized, k, v);
            }
        });
        return normalized;
    }

    public static Map<String, List<String>> sanitizeMultiValueParams(Map<String, List<String>> params) {
        Map<String, List<String>> normalized = new LinkedHashMap<>();
        if (CollectionUtil.isEmpty(params)) {
            return normalized;
        }
        params.forEach((k, vs) -> {
            if (Objects.nonNull(k) && CollectionUtil.isNotEmpty(vs)) {
                vs.stream()
                        .filter(Objects::nonNull)
                        .forEach(v -> addMultiValue(normalized, k, v));
            }
        });
        return normalized;
    }

    public static void addSingleHeader(Map<String, List<String>> headers, String key, String value) {
        if (StrUtil.isBlank(key) || Objects.isNull(value)) {
            return;
        }
        addMultiValue(headers, key, value);
    }

    public static void addMultiHeader(Map<String, List<String>> headers, String key, List<String> values) {
        if (StrUtil.isBlank(key) || CollectionUtil.isEmpty(values)) {
            return;
        }
        values.stream()
                .filter(Objects::nonNull)
                .forEach(v -> addMultiValue(headers, key, v));
    }

    public static String appendQuery(String fullUrl, Map<String, List<String>> params) {
        if (CollectionUtil.isEmpty(params)) {
            return fullUrl;
        }
        String query = buildMultiValueQuery(params);
        if (StrUtil.isBlank(query)) {
            return fullUrl;
        }
        if (StrUtil.contains(fullUrl, "?")) {
            return StrUtil.endWithAny(fullUrl, "?", "&") ? fullUrl + query : fullUrl + "&" + query;
        }
        return fullUrl + "?" + query;
    }

    public static String completeUrlWithQuery(String host, String uri, String queryString) {
        String url = URLUtil.completeUrl(host, uri);
        return StrUtil.isBlank(queryString) ? url : url + "?" + queryString;
    }

    private static String buildMultiValueQuery(Map<String, List<String>> params) {
        StringBuilder queryBuilder = new StringBuilder();
        params.forEach((k, values) -> {
            if (CollectionUtil.isEmpty(values)) {
                return;
            }
            for (String value : values) {
                if (queryBuilder.length() > 0) {
                    queryBuilder.append("&");
                }
                String encodedKey = URLUtil.encode(k, StandardCharsets.UTF_8);
                String encodedValue = (value == null) ? "" : URLUtil.encode(value, StandardCharsets.UTF_8);
                queryBuilder.append(encodedKey).append("=").append(encodedValue);
            }
        });
        return queryBuilder.toString();
    }

    private static void addMultiValue(Map<String, List<String>> target, String key, String value) {
        List<String> values = target.computeIfAbsent(key, k -> new ArrayList<>());
        values.add(value);
    }
}

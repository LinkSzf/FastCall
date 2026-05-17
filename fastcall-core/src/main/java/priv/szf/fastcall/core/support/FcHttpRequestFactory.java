package priv.szf.fastcall.core.support;

import cn.hutool.json.JSONUtil;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.core.auth.FcRequestContext;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class FcHttpRequestFactory {

    private FcHttpRequestFactory() {
    }

    public static Request createRequest(
            String system,
            FcAuthType authType,
            FcCallType callType,
            String url,
            FcRequestMethod method,
            FcMediaType contentType,
            Object body,
            Map<String, List<String>> headers
    ) {
        Headers requestHeaders = buildHeaders(headers);
        RequestBody requestBody = buildRequestBody(body, contentType, method);
        FcRequestContext requestContext = FcRequestContext.builder()
                .system(system)
                .authType(authType)
                .callType(callType)
                .build()
                .check();

        return new Request.Builder()
                .tag(FcRequestContext.class, requestContext)
                .url(url)
                .headers(requestHeaders)
                .method(method.getName(), requestBody)
                .build();
    }

    private static Headers buildHeaders(Map<String, List<String>> headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (Objects.nonNull(headers)) {
            headers.forEach((key, values) -> {
                if (Objects.nonNull(values)) {
                    values.forEach(value -> headerBuilder.add(key, value));
                }
            });
        }
        return headerBuilder.build();
    }

    private static RequestBody buildRequestBody(Object body, FcMediaType contentType, FcRequestMethod method) {
        return Optional.ofNullable(body)
                .map(payload -> {
                    if (payload instanceof RequestBody) {
                        return (RequestBody) payload;
                    }

                    FcMediaType targetContentType = (Objects.isNull(contentType))
                            ? FcMediaType.APPLICATION_JSON
                            : contentType;
                    MediaType mediaType = MediaType.parse(targetContentType.getName());
                    if (payload instanceof byte[]) {
                        return RequestBody.create((byte[]) payload, mediaType);
                    }
                    if (payload instanceof File) {
                        return RequestBody.create((File) payload, mediaType);
                    }
                    String jsonStr = JSONUtil.toJsonStr(payload);
                    return RequestBody.create(jsonStr, mediaType);
                })
                .orElse(
                        method == FcRequestMethod.GET ? null
                                : RequestBody.create(new byte[0])
                );
    }
}

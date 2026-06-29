package priv.szf.fastcall.core.support;

import cn.hutool.json.JSONUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.core.FastCallClient;
import priv.szf.fastcall.core.auth.FcRequestContext;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FcHttpRequestSupport {


    public static Request createRequest(FastCallClient.Builder<?> builder, String system) {
        Headers requestHeaders = buildHeaders(builder.getHeaders());
        RequestBody requestBody = buildRequestBody(builder.getBody(), builder.getContentType(), builder.getMethod());
        FcRequestContext requestContext = FcRequestContext.builder()
                .system(system)
                .authType(builder.getAuthType())
                .callType(builder.getCallType())
                .build()
                .check();

        return new Request.Builder()
                .tag(FcRequestContext.class, requestContext)
                .tag(FastCallClient.class, builder.getClient())
                .url(builder.getFullUrl())
                .headers(requestHeaders)
                .method(builder.getMethod().getName(), requestBody)
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

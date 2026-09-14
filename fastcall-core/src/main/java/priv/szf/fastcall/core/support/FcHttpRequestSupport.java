package priv.szf.fastcall.core.support;

import lombok.RequiredArgsConstructor;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.json.FcJsonCodec;
import priv.szf.fastcall.core.FastCallClient;
import priv.szf.fastcall.core.auth.FcRequestContext;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class FcHttpRequestSupport {

    private final FcJsonCodec jsonCodec;


    public Request createRequest(FastCallClient.Builder<?> builder, String system) {
        Headers requestHeaders = buildHeaders(builder.getHeaders());
        RequestBody requestBody = buildRequestBody(builder.getBody(), builder.getContentType(), builder.getMethod());
        FcRequestContext requestContext = FcRequestContext.builder()
                .system(system)
                .authType(builder.getAuthType())
                .callType(builder.getCallType())
                .client(builder.getClient())
                .source(builder.getSourcePak())
                .build()
                .check();

        return new Request.Builder()
                .tag(FcRequestContext.class, requestContext)
                .url(builder.getFullUrl())
                .headers(requestHeaders)
                .method(builder.getMethod().getName(), requestBody)
                .build();
    }

    private Headers buildHeaders(Map<String, List<String>> headers) {
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

    private RequestBody buildRequestBody(Object body, FcMediaType contentType, FcRequestMethod method) {
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
                    return RequestBody.create(jsonCodec.write(payload), mediaType);
                })
                .orElse(
                        method == FcRequestMethod.GET ? null
                                : RequestBody.create(new byte[0])
                );
    }
}

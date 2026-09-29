package io.github.linkszf.fastcall.core.support;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import io.github.linkszf.fastcall.common.FcMediaType;
import io.github.linkszf.fastcall.common.FcRequestMethod;
import io.github.linkszf.fastcall.common.exception.FastCallException;
import io.github.linkszf.fastcall.common.json.FcJsonCodec;
import io.github.linkszf.fastcall.core.FastCallClient;
import io.github.linkszf.fastcall.core.auth.FcRequestContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class FcHttpRequestSupport {

    private static final String DEFAULT_PART_NAME = "file";

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
                    if (payload instanceof MultipartFile) {
                        return buildMultipartFileBody((MultipartFile) payload);
                    }
                    return RequestBody.create(jsonCodec.write(payload), mediaType);
                })
                .orElse(
                        method == FcRequestMethod.GET ? null
                                : RequestBody.create(new byte[0])
                );
    }

    /**
     * When a {@link MultipartFile} is used as the request body, it is sent as a single-file part of {@code multipart/form-data}:
     * The part name is the form field name, the file name is the original file name, and the part content type prefers the type declared by the file itself.
     * To send raw binary content without a form structure, pass {@code byte[]}/{@code File}/{@code InputStream} directly.
     */
    private RequestBody buildMultipartFileBody(MultipartFile payload) {
        String partName = StrUtil.blankToDefault(StrUtil.trim(payload.getName()), DEFAULT_PART_NAME);
        String fileName = StrUtil.blankToDefault(StrUtil.trim(payload.getOriginalFilename()), partName);
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(partName, fileName, RequestBody.create(readBytes(payload), resolvePartMediaType(payload)))
                .build();
    }

    private MediaType resolvePartMediaType(MultipartFile payload) {
        MediaType partMediaType = MediaType.parse(StrUtil.trimToEmpty(payload.getContentType()));
        if (Objects.nonNull(partMediaType)) {
            return partMediaType;
        }
        return MediaType.parse(FcMediaType.APPLICATION_OCTET_STREAM.getName());
    }

    private byte[] readBytes(MultipartFile payload) {
        try {
            return payload.getBytes();
        } catch (IOException e) {
            throw new FastCallException(e, "Failed to read bytes of multipart file[{}]", payload.getOriginalFilename());
        }
    }
}

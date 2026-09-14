package priv.szf.fastcall.core.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.json.FcJsonCodec;
import priv.szf.fastcall.common.utils.FcTimeUtil;
import priv.szf.fastcall.core.FastCallResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class FcHttpResponseSupport {

    private final FcJsonCodec jsonCodec;


    public <T> FastCallResponse<T> buildStandardResponse(Type dataType, Response response) {
        int code = response.code();
        String message = response.message();
        boolean isSuccessful = response.isSuccessful();
        ResponseBody body = response.body();
        T data = readResponseBodyData(dataType, body);
        String mediaType = Optional.ofNullable(body)
                .map(ResponseBody::contentType)
                .map(Object::toString)
                .orElse(null);
        Headers headers = response.headers();
        Map<String, List<String>> headersMap = headers.toMultimap();
        LocalDateTime requestTime = FcTimeUtil.ofMillis(response.sentRequestAtMillis());
        LocalDateTime responseTime = FcTimeUtil.ofMillis(response.receivedResponseAtMillis());
        boolean isRedirect = response.isRedirect();
        boolean isCached = Objects.nonNull(response.cacheResponse());

        return FastCallResponse.<T>builder()
                .code(code)
                .message(message)
                .isSuccessful(isSuccessful)
                .data(data)
                .mediaType(mediaType)
                .headers(headersMap)
                .isConnected(true)
                .isCached(isCached)
                .isRedirect(isRedirect)
                .requestTime(requestTime)
                .responseTime(responseTime)
                .build();
    }

    private <T> T readResponseBodyData(Type dataType, ResponseBody body) {
        if (Objects.isNull(body)) {
            return null;
        }

        Type targetType = (Objects.isNull(dataType)) ? Object.class : dataType;

        try {
            Class<?> rawType = getRawType(targetType);
            MediaType contentType = body.contentType();

            if (rawType == byte[].class) {
                return (T) body.bytes();
            }

            if (rawType == InputStream.class) {
                return (T) new ByteArrayInputStream(body.bytes());
            }

            String bodyStr = body.string();
            if (rawType == String.class || rawType == Object.class) {
                return (T) bodyStr;
            }

            if (StrUtil.isBlank(bodyStr)) {
                return null;
            }

            boolean isJsonPayload = isJsonMediaType(contentType) || JSONUtil.isTypeJSON(bodyStr);
            if (!isJsonPayload) {
                throw new FcUnexpectedException(
                        "Response content type[{}] is not JSON, cannot convert to type[{}], body length[{}]",
                        Objects.toString(contentType, "null"),
                        targetType.getTypeName(),
                        bodyStr.length()
                );
            }

            try {
                return (T) jsonCodec.read(bodyStr, targetType);
            } catch (FcUnexpectedException e) {
                throw e;
            } catch (RuntimeException e) {
                throw new FcUnexpectedException(
                        e,
                        "JSON response cannot be converted to type[{}], body length[{}]",
                        targetType.getTypeName(),
                        bodyStr.length()
                );
            }
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "IO exception occurred when reading response body");
        } catch (ClassCastException e) {
            throw new FcUnexpectedException(e,
                    "Response body cannot be converted to type[{}]]",
                    targetType.getTypeName());
        }
    }

    private boolean isJsonMediaType(MediaType mediaType) {
        return Objects.nonNull(mediaType) && StrUtil.containsIgnoreCase(mediaType.toString(), "json");
    }

    private Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            Type raw = ((ParameterizedType) type).getRawType();
            if (raw instanceof Class) {
                return (Class<?>) raw;
            }
        }
        return Object.class;
    }
}

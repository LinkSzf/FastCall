package priv.szf.fastcall.core.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
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

public final class FcHttpResponseMapper {

    private static final ObjectMapper OBJECT_MAPPER = initObjectMapper();

    private FcHttpResponseMapper() {
    }

    private static ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }

    public static <T> FastCallResponse<T> buildStandardResponse(Type dataType, String url, Response response) {
        int code = response.code();
        String message = response.message();
        boolean isSuccessful = response.isSuccessful();
        ResponseBody body = response.body();
        T data = readResponseBodyData(dataType, body, url);
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

    private static <T> T readResponseBodyData(Type dataType, ResponseBody body, String url) {
        if (Objects.isNull(body)) {
            return null;
        }

        try {
            Class<?> rawType = getRawType(dataType);
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
                        "Response content type[{}] is not JSON, cannot convert to type[{}] in url[{}], body length[{}]",
                        Objects.toString(contentType, "null"),
                        dataType.getTypeName(),
                        url,
                        bodyStr.length()
                );
            }

            try {
                return (T) OBJECT_MAPPER.readValue(bodyStr, rawType);
            } catch (RuntimeException e) {
                throw new FcUnexpectedException(
                        e,
                        "JSON response cannot be converted to type[{}] in url[{}], body length[{}]",
                        dataType.getTypeName(),
                        url,
                        bodyStr.length()
                );
            }
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "IO exception occurred when reading response body in url[{}]", url);
        } catch (ClassCastException e) {
            throw new FcUnexpectedException(e,
                    "Response body cannot be converted to type[{}] in url[{}]",
                    dataType.getTypeName(),
                    url);
        }
    }

    private static boolean isJsonMediaType(MediaType mediaType) {
        return Objects.nonNull(mediaType) && StrUtil.containsIgnoreCase(mediaType.toString(), "json");
    }

    private static Class<?> getRawType(Type type) {
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

package priv.szf.fastcall.core.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.http.ContentDisposition;
import org.springframework.web.multipart.MultipartFile;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class FcHttpResponseSupport {

    private static final String DEFAULT_FILE_NAME = "file";

    /**
     * RFC 5987 形式的文件名：filename*=UTF-8''%E4%B8%AD%E6%96%87.txt
     */
    private static final Pattern FILENAME_STAR_PATTERN = Pattern.compile(
            "filename\\*\\s*=\\s*([^;]+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern FILENAME_QUOTED_PATTERN = Pattern.compile(
            "filename\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

    private static final Pattern FILENAME_TOKEN_PATTERN = Pattern.compile(
            "filename\\s*=\\s*([^;\"\\s]+)", Pattern.CASE_INSENSITIVE);

    private final FcJsonCodec jsonCodec;


    public <T> FastCallResponse<T> buildStandardResponse(Type dataType, Response response) {
        int code = response.code();
        String message = response.message();
        boolean isSuccessful = response.isSuccessful();
        ResponseBody body = response.body();
        T data = readResponseBodyData(dataType, response);
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

    private <T> T readResponseBodyData(Type dataType, Response response) {
        ResponseBody body = response.body();
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

            if (MultipartFile.class.isAssignableFrom(rawType)) {
                // MultipartFile 以文件语义承载响应体，始终按二进制内容读取，不参与 JSON 反序列化
                byte[] content = body.bytes();
                if (content.length == 0) {
                    return null;
                }
                String fileName = resolveFileName(response);
                return (T) new FcMultipartFile(fileName, fileName, Objects.toString(contentType, null), content);
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

    /**
     * 解析下载文件名：优先 {@code Content-Disposition}，其次 URL 末段，最后退化为 {@link #DEFAULT_FILE_NAME}。
     */
    private String resolveFileName(Response response) {
        String fileName = parseDispositionFileName(response.header("Content-Disposition"));
        if (StrUtil.isBlank(fileName)) {
            fileName = resolveUrlFileName(response);
        }
        return StrUtil.blankToDefault(StrUtil.trim(fileName), DEFAULT_FILE_NAME);
    }

    private String parseDispositionFileName(String disposition) {
        if (StrUtil.isBlank(disposition)) {
            return null;
        }

        try {
            String fileName = ContentDisposition.parse(disposition).getFilename();
            if (StrUtil.isNotBlank(fileName)) {
                return fileName;
            }
        } catch (IllegalArgumentException ignore) {
            // 头部不合规时退化为手工解析
        }

        String encodedFileName = matchGroup(FILENAME_STAR_PATTERN, disposition);
        if (StrUtil.isNotBlank(encodedFileName)) {
            int charsetEnd = encodedFileName.indexOf("''");
            String encodedValue = (charsetEnd < 0) ? encodedFileName : encodedFileName.substring(charsetEnd + 2);
            return URLUtil.decode(encodedValue);
        }

        String quotedFileName = matchGroup(FILENAME_QUOTED_PATTERN, disposition);
        if (StrUtil.isNotBlank(quotedFileName)) {
            return quotedFileName;
        }
        return matchGroup(FILENAME_TOKEN_PATTERN, disposition);
    }

    private String matchGroup(Pattern pattern, String source) {
        Matcher matcher = pattern.matcher(source);
        if (!matcher.find()) {
            return null;
        }
        return StrUtil.trim(matcher.group(1));
    }

    private String resolveUrlFileName(Response response) {
        Request request = response.request();
        if (Objects.isNull(request)) {
            return null;
        }

        HttpUrl url = request.url();
        List<String> segments = url.pathSegments();
        if (segments.isEmpty()) {
            return null;
        }
        return StrUtil.trim(segments.get(segments.size() - 1));
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

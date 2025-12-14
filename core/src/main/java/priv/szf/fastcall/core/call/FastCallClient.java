package priv.szf.fastcall.core.call;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.call.auth.FcCallType;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.FcMediaType;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.common.FcRequestMethod;
import priv.szf.fastcall.core.model.FcSystemPak;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class FastCallClient {

    private final OkHttpClient client;

    private final FcSourcePak source;
    private <T> FastCallResponse<T> call(Builder<T> builder) {
        FcSystemPak system = source.getSystem();

        Headers.Builder headerBuilder = new Headers.Builder();
        builder.getHeaders().forEach(headerBuilder::add);
        Headers headers = headerBuilder.build();

        RequestBody requestBody = RequestBody.create(
                new JSONObject(builder.getBody()).toString(),
                MediaType.parse(builder.getMediaType().getName())
        );

        String url = builder.getFullUrl();

        Request request = new Request.Builder()
                .tag(FcCallType.class, builder.getCallType())
                .url(url)
                .headers(headers)
                .method(builder.getMethod().getName(), requestBody)
                .build();

        TypeReference<T> dataType = new TypeReference<T>() {};
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            int code = response.code();
            String message = response.message();
            boolean isSuccessful = response.isSuccessful();
            ResponseBody body = response.body();
            T data = null;
            if (Objects.nonNull(body)) {
                try {
                    if (String.class == dataType.getType()) {
                        data = (T) body.string();
                    }
                    else {
                        data = (T) JSONUtil.toBean(body.string(), dataType.getType().getClass());
                    }
                } catch (ClassCastException e) {
                    throw new FcUnexpectedException(e,
                            String.format("FastCall-url[%s]请求失败，类型转换失败，无法将[%s]转换为类型[%s]",
                                    url,
                                    body.string(),
                                    dataType.getType().getTypeName()));
                }
            }

            return FastCallResponse.<T>builder()
                    .code(code)
                    .message(message)
                    .isSuccessful(isSuccessful)
                    .data(data)
                    .build();

        } catch (IOException e) {
            throw new FcUnexpectedException(e, String.format("FastCall-url[%s]请求失败，IO异常", url));
        }
    }

    public <T> Builder<T> newCall() {
        return new Builder<T>(this);
    }

    @Getter
    public class Builder<T> {

        private final Map<String, String> headers = new HashMap<>();

        private final FastCallClient client;

        private FcRequestMethod method = FcRequestMethod.GET;

        private FcMediaType mediaType = FcMediaType.ALL;

        private FcCallType callType = FcCallType.NORMAL;

        private String url;

        private String fullUrl;

        private Map<String, String> params;

        private Object body;

        private Builder(FastCallClient client) {
            this.client = client;
        }

        public Builder<T> url(String url) {
            this.url = Optional.ofNullable(url)
                    .filter(StringUtils::isNotBlank)
                    .map(URLUtil::normalize)
                    .orElseThrow(() -> new FcUnexpectedException("FastCall-请求地址不能为空"));
            return this;
        }

        public Builder<T> params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder<T> method(FcRequestMethod requestMethod) {
            this.method = (Objects.isNull(requestMethod)) ? FcRequestMethod.GET : requestMethod;
            return this;
        }

        public Builder<T> header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder<T> header(FcHttpHeader key, FcMediaType value) {
            if (Objects.isNull(key) || Objects.isNull(value)) {
                return this;
            }
            return header(key.getName(), value.getName());
        }

        public Builder<T> body(Object body) {
            String bodyStr = JSONUtil.toJsonStr(body);
            return body(bodyStr, FcMediaType.APPLICATION_JSON);
        }

        public Builder<T> body(Object body, FcMediaType mediaType) {
            this.body = body;
            this.mediaType = mediaType;
            return this;
        }

        public FastCallResponse<T> call() {
            if (CollectionUtil.isNotEmpty(params)) {
                this.fullUrl = url + URLUtil.buildQuery(params, StandardCharsets.UTF_8);
            } else {
                this.fullUrl = url;
            }
            return client.call(this);
        }

        public FastCallResponse<T> anonymousCall() {
            this.callType = FcCallType.AUTH;
            return call();
        }

    }


}

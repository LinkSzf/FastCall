package priv.szf.fastcall.core.call;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import priv.szf.fastcall.core.call.auth.FcCallType;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.FcMediaType;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.common.FcRequestMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class FastCallClient {

    private final OkHttpClient client;

    private final FcSourcePak source;
    private <T> FastCallResponse<T> call(Builder<T> builder) {
        Headers.Builder headerBuilder = new Headers.Builder();
        builder.headers.forEach(headerBuilder::add);
        Headers headers = headerBuilder.build();

        FcRequestMethod method = builder.method;
        RequestBody requestBody = (method == FcRequestMethod.GET) ? null :
                RequestBody.create(
                        new JSONObject(builder.body).toString(),
                        MediaType.parse(builder.mediaType.getName())
                );

        String url = builder.fullUrl;

        Request request = new Request.Builder()
                .tag(FcSourcePak.class, source)
                .tag(FcCallType.class, builder.callType)
                .url(url)
                .headers(headers)
                .method(method.getName(), requestBody)
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            int code = response.code();
            String message = response.message();
            boolean isSuccessful = response.isSuccessful();
            ResponseBody body = response.body();
            T data = null;
            Class<T> dataType = builder.dataType;
            if (Objects.nonNull(body)) {
                String bodyStr = body.string();
                try {
                    if (String.class == dataType || Object.class == dataType) {
                        data = (T) bodyStr;
                    }
                    else if (JSONUtil.isTypeJSON(bodyStr)) {
                        data = JSONUtil.toBean(bodyStr, dataType);
                    }
                } catch (ClassCastException e) {
                    throw new FcUnexpectedException(e,
                            String.format("FastCall-url[%s]请求失败，类型转换失败，无法将[%s]转换为类型[%s]",
                                    url,
                                    bodyStr,
                                    dataType.getName()));
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

    public <T> Builder<T> newCall(Class<T> dataType) {
        return new Builder<>(this, dataType);
    }

    public <T> Builder<T> newCall() {
        return new Builder<>(this, (Class<T>)Object.class);
    }

    public class Builder<T> {

        private final Map<String, String> headers = new HashMap<>();

        private final FastCallClient client;

        private final Class<T> dataType;

        private final String host;

        private FcRequestMethod method = FcRequestMethod.GET;

        private FcMediaType mediaType = FcMediaType.ALL;

        private FcCallType callType = FcCallType.NORMAL;

        private String url = "/";

        private String fullUrl;

        private Map<String, String> params;

        private Object body;

        private Builder(FastCallClient client, Class<T> dataType) {
            this.client = client;
            this.dataType = dataType;
            this.host = source.getSystem().getHost();
        }

        public Builder<T> url(String url) {
            this.url = url;
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
            this.fullUrl = URLUtil.completeUrl(host, url);
            if (CollectionUtil.isNotEmpty(params)) {
                this.fullUrl = this.fullUrl + URLUtil.buildQuery(params, StandardCharsets.UTF_8);
            }
            return client.call(this);
        }

        public FastCallResponse<T> anonymousCall() {
            this.callType = FcCallType.ANONYMOUS;
            return call();
        }

    }


}

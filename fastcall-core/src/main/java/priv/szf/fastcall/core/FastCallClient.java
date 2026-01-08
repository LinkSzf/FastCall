package priv.szf.fastcall.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Builder;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.source.IFcSource;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcApiParamPak;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Builder
public class FastCallClient {

    private final OkHttpClient client;

    private final String system;

    private final FcAuthType authType;

    private final IFcSource source;

    private static final ThreadLocal<Builder<?>> LOCAL_BUILDER = new ThreadLocal<>();

    private <T> Request createRequest(Builder<T> builder) {
        Headers.Builder headerBuilder = new Headers.Builder();
        builder.headers.forEach(headerBuilder::add);
        Headers headers = headerBuilder.build();

        FcRequestMethod method = builder.method;
        RequestBody requestBody = (method == FcRequestMethod.GET) ? null :
                RequestBody.create(
                        new JSONObject(builder.body).toString(),
                        MediaType.parse(builder.contentType.getName())
                );

        String url = builder.fullUrl;

        FcRequestContext requestContext = FcRequestContext.builder()
                .system(system)
                .authType(authType)
                .callType(builder.callType)
                .build()
                .check();

        return new Request.Builder()
                .tag(FcRequestContext.class, requestContext)
                .url(url)
                .headers(headers)
                .method(method.getName(), requestBody)
                .build();
    }

    private <T> FastCallResponse<T> buildStandardResponse(Builder<T> builder, Response response) {
        int code = response.code();
        String message = response.message();
        boolean isSuccessful = response.isSuccessful();
        ResponseBody body = response.body();
        T data = readResponseBodyData(builder.dataType, body, builder.fullUrl);
        Headers headers = response.headers();
        Map<String, List<String>> headersMap = headers.toMultimap();


        return FastCallResponse.<T>builder()
                .code(code)
                .message(message)
                .isSuccessful(isSuccessful)
                .data(data)
                .headers(headersMap)
                .build();
    }

    private <T> FastCallResponse<T> doCall(Builder<T> builder) {
        Request request = createRequest(builder);

        Call call = client.newCall(request);

        try (Response response = call.execute()) {
            return buildStandardResponse(builder, response);
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "url[%s]请求失败，IO异常", builder.fullUrl);
        }
    }

    private <T> T readResponseBodyData(Class<T> dataType, ResponseBody body, String url) {
        if (Objects.isNull(body)) {
            return null;
        }

        T data = null;
        String bodyStr = null;
        try {
            bodyStr = body.string();
            if (String.class == dataType || Object.class == dataType) {
                data = (T) bodyStr;
            } else if (JSONUtil.isTypeJSON(bodyStr)) {
                data = JSONUtil.toBean(bodyStr, dataType);
            }
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "url[%s]请求失败，读取响应体字符串时IO异常", url);
        } catch (ClassCastException e) {
            throw new FcUnexpectedException(e,
                            "url[%s]请求失败，类型转换失败，无法将[%s]转换为类型[%s]",
                            url,
                            bodyStr,
                            dataType.getName());
        }
        return data;
    }

    public <T> Builder<T> newCall(Class<T> dataType) {
        return new Builder<>(this, dataType);
    }

    public <T> Builder<T> newCall() {
        return new Builder<>(this, (Class<T>)Object.class);
    }

    public FastCallClient newApiCall(String apiName, FcApiParamPak params) {
        Map<String, FcApiPak> apiMap = source.getSourcePak(system).getApiMap();
        FcApiPak api = apiMap.get(apiName);
        if (Objects.isNull(api)) {
            throw new FastCallException("apiName[%s]不存在", apiName);
        }

        FcApiParamPak defaultParams = api.getParams();
        FcApiParamPak finalParams = defaultParams.mergeBy(params);

        return newCall()
                .host(api.getParticularHost())
                .uri(api.getPath())
                .method(api.getMethod())
                .headers(finalParams.getHeaders())
                .params(finalParams.getParams())
                .body(finalParams.getBody())
                .prepared();
    }

    public FastCallClient newApiCall(String apiName) {
        return newApiCall(apiName, null);
    }

    public <T> FastCallResponse<T> callIt() {
        Builder<T> builder = (Builder<T>) LOCAL_BUILDER.get();
        try {
            return doCall(builder);
        }
        finally {
            LOCAL_BUILDER.remove();
        }
    }

    public <T> FastCallResponse<T> anonymousCallIt() {
        Builder<T> builder = (Builder<T>) LOCAL_BUILDER.get();
        builder.callType = FcCallType.ANONYMOUS;
        return callIt();
    }

    public <T> T call() {
        FastCallResponse<T> call = callIt();
        return call.getData();
    }

    public <T> T anonymousCall() {
        FastCallResponse<T> call = anonymousCallIt();
        return call.getData();
    }

    public class Builder<T> {

        private final Map<String, String> headers = new HashMap<>();

        private final FastCallClient client;

        private final Class<T> dataType;

        private FcRequestMethod method = FcRequestMethod.GET;

        private FcMediaType contentType = FcMediaType.APPLICATION_JSON;

        private FcCallType callType = FcCallType.NORMAL;

        private String host;

        private String uri;

        private String url;

        private String fullUrl;

        private Map<String, String> params;

        private Object body;

        private Builder(FastCallClient client, Class<T> dataType) {
            this.client = client;
            this.dataType = dataType;
            this.host = source.getSourcePak(system).getSystem().getHost();
        }

        public Builder<T> url(String url) {
            this.url = url;
            return this;
        }

        public Builder<T> host(String host) {
            if (StrUtil.isNotBlank(host)) {
                this.host = host;
            }
            return this;
        }

        public Builder<T> uri(String uri) {
            this.uri = uri;
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

        public Builder<T> headers(Map<String, String> headers) {
            if (CollectionUtil.isNotEmpty(headers)) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public Builder<T> body(Object body) {
            if (Objects.isNull(body)) {
                return this;
            }
            String bodyStr = JSONUtil.toJsonStr(body);
            return body(bodyStr, FcMediaType.APPLICATION_JSON);
        }

        public Builder<T> body(Object body, FcMediaType mediaType) {
            this.body = body;
            this.contentType = mediaType;
            return this;
        }

        public FastCallClient prepared() {
            this.fullUrl = (StrUtil.isNotBlank(this.url)) ? this.url
                    : URLUtil.completeUrl(host, uri);

            if (CollectionUtil.isNotEmpty(params)) {
                this.fullUrl = this.fullUrl + URLUtil.buildQuery(params, StandardCharsets.UTF_8);
            }

            LOCAL_BUILDER.set(this);

            return this.client;
        }

    }


}

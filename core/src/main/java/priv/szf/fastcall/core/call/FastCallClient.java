package priv.szf.fastcall.core.call;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import priv.szf.fastcall.core.call.auth.FcCallType;
import priv.szf.fastcall.core.call.auth.BaseDynAuthContent;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.FcMediaType;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.common.FcRequestMethod;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class FastCallClient {

    private final OkHttpClient client;

    private final FcSourcePak source;

//    private final FcSystemPak system;
//
//    private final FcAuthPak auth;
//
//    private final FcTokenPak token;

    private final ThreadLocal<FcApiPak> api = new ThreadLocal<>();

//    public FastCallClient(OkHttpClient client, FcSystemPak system, FcAuthPak auth, FcTokenPak accessToken) {
//        this.client = client;
//        this.system = system;
//        this.auth = auth;
//        this.token = accessToken;
//    }

//    public FastCallClient(OkHttpClient client, FcSourcePak source) {
//        this.client = client;
//        this.source = source;
//    }

    public <T> FastCallResult<T> call() {
        Request request = new Request.Builder()
                .url("https://api.example.com/protected-resource")
                .tag(FcSourcePak.class, source)
                .tag(AuthType.class, source.getAuth().getType())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T call(Builder<T> builder) {
        return null;
    }

    public <T> Builder<T> newCall() {
        return new Builder<T>(this);
    }

    public FcTokenPak doAuth() {
        FcSystemPak system = source.getSystem();
        FcAuthPak auth = source.getAuth();
        BaseAuthContent content = auth.getContent();
        if (!(content instanceof BaseDynAuthContent)) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]的认证方式不支持刷新", system.getName())
            );
        }

        Map<String, String> params = ((BaseDynAuthContent) content).getParams();
        RequestBody requestBody = RequestBody.create(
                new JSONObject(params).toString(),
                MediaType.parse("application/json")
        );
        
        
        String host = StringUtils.isBlank(auth.getParticularHost()) ? system.getHost() : auth.getParticularHost();
        String url = URLUtil.completeUrl(host, auth.getPath());
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(requestBody)
                .tag(FcCallType.class, FcCallType.AUTH)
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            String tokenField = ((BaseDynAuthContent) content).getTokenField();
            ResponseBody responseBody = getResponseBody(response, system);
            JSONObject jsonResponse = JSONUtil.parseObj(responseBody.string());
            String token = jsonResponse.getByPath(tokenField, String.class);
            if (StringUtils.isBlank(token)) {
                throw new FcUnexpectedException(
                        String.format("FastCall-系统[%s]刷新认证失败，响应体中路径[%s]未找到token字段",
                                system.getName(), tokenField)
                );
            }

            return FcTokenPak.builder()
                    .token(token)
                    .issuance(LocalDateTime.now())
                    .estimatedExpiration(LocalDateTime.now().plusSeconds(auth.getExpiration()))
                    .build();
        } catch (IOException e) {
            throw new FcUnexpectedException(e, String.format("FastCall-系统[%s]刷新认证失败，IO异常",  system.getName()));
        }
    }

    private ResponseBody getResponseBody(Response response, FcSystemPak system) {
        if (!response.isSuccessful()) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，code[%s], message[%s]",
                            system.getName(), response.code(), response.message())
            );
        }

        ResponseBody body = response.body();
        if (Objects.isNull(body)) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，响应体为空", system.getName())
            );
        }

        return body;
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

        private Map<String, String> respone;

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

        public Builder<T> response(Map<String, String> responseMap) {
            this.respone = responseMap;
            return this;
        }

        public T call() {
            if (CollectionUtil.isNotEmpty(params)) {
                this.fullUrl = URLUtil.buildQuery(params, url);
            } else {
                this.fullUrl = url;
            }
            return client.call(this);
        }

        public T anonymousCall() {
            this.callType = FcCallType.AUTH;
            return call();
        }
    }


}

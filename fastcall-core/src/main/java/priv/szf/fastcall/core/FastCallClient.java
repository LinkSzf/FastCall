package priv.szf.fastcall.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.Builder;
import lombok.Getter;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.core.filter.FcFilterContext;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.core.filter.FcFilterManager;
import priv.szf.fastcall.core.support.FcFilterContextSupport;
import priv.szf.fastcall.core.support.FcHttpRequestSupport;
import priv.szf.fastcall.core.support.FcHttpResponseSupport;
import priv.szf.fastcall.core.support.FcRequestBuildSupport;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Builder
public class FastCallClient {

    private final OkHttpClient client;

    private final String system;

    private final IFcSource source;

    private final FcFilterManager filterManager;

    private <T> FastCallResponse<T> doCall(Builder<T> builder, boolean throwException) {
        Request request = FcHttpRequestSupport.createRequest(builder, system);

        Call call = this.client.newCall(request);

        try (Response response = call.execute()) {
            return FcHttpResponseSupport.buildStandardResponse(builder.dataType, builder.fullUrl, response);
        } catch (Exception e) {
            if (throwException) {
                throw new FcUnexpectedException(e, "Unexpected exception occurred when calling url[{}]", request.url());
            }
            return FastCallResponse.<T>builder()
                    .isSuccessful(false)
                    .isConnected(false)
                    .exception(e)
                    .requestTime(LocalDateTime.now())
                    .build();
        }
    }

    public <T> PreparedCall<T> newApiCall(String apiName) {
        return this.<T>newCall()
                .apiName(apiName)
                .prepared();
    }

    public <T> Builder<T> newCall(Class<T> dataType) {
        return this.newCall((Type) dataType);
    }

    public <T> Builder<T> newCall() {
        return this.<T>newCall(Object.class);
    }

    public <T> Builder<T> newCall(Type dataType) {
        FcSourcePak sourcePak = this.source.getSourcePak(this.system);
        Builder<T> builder = new Builder<>(this, sourcePak, dataType);
        if (Objects.nonNull(builder.apiName)) {
            Optional.of(sourcePak)
                    .map(FcSourcePak::getApiMap)
                    .map(apiMap -> apiMap.get(builder.apiName))
                    .ifPresent(api -> {
                        builder.host(api.getParticularHost())
                                .uri(api.getPath())
                                .method(api.getMethod());
                        Optional.ofNullable(api.getParamPak())
                                .ifPresent(paramPak -> {
                                    builder
                                            .headers(paramPak.getHeaders())
                                            .params(paramPak.getParams())
                                            .body(paramPak.getBody());
                                });
                    });

        }

        String host = Optional.of(sourcePak).filter(pak -> Objects.isNull(builder.host)).map(FcSourcePak::getSystem).map(FcSystemPak::getHost).orElse(null);
        FcAuthType authType = Optional.of(sourcePak).map(FcSourcePak::getSystem).map(FcSystemPak::getAuthType).orElse(FcAuthType.NONE);
        builder.authType(authType)
                .host(host);
        return builder;
    }

    private <T> FastCallResponse<T> callIt(Builder<T> builder, boolean throwException) {
        AtomicReference<FastCallResponse<T>> responseRef = new AtomicReference<>();
        FcFilterContext context = FcFilterContextSupport.createFilterContext(builder, this.system);
        this.filterManager.doFilter(context, ctx -> {
            FastCallResponse<T> response = doCall(builder, throwException);
            responseRef.set(response);
            ctx.setResponse(response);
        });
        return responseRef.get();
    }

    @Getter
    public static class Builder<T> {

        private final FastCallClient client;

        private final FcSourcePak sourcePak;

        private final Type dataType;

        private final Map<String, List<String>> headers = new HashMap<>();

        private FcRequestMethod method = FcRequestMethod.GET;

        private FcMediaType contentType = FcMediaType.APPLICATION_JSON;

        private FcCallType callType = FcCallType.NORMAL;

        private boolean isAuth = false;

        private FcAuthType authType;

        private String apiName;

        private String host;

        private String uri;

        private String url;

        private String fullUrl;

        private Map<String, List<String>> params;

        private Object body;

        private Builder(FastCallClient client, FcSourcePak sourcePak, Type dataType) {
            this.client = client;
            this.sourcePak = sourcePak;
            this.dataType = dataType;
        }

        private Builder<T> apiName(String apiName) {
            this.apiName = apiName;
            return this;
        }

        public Builder<T> authType(FcAuthType authType) {
            this.authType = authType;
            return this;
        }

        public Builder<T> isAuth(boolean isAuth) {
            this.isAuth = isAuth;
            return this;
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
            if (StrUtil.isNotBlank(host)) {
                this.uri = uri;
            }
            return this;
        }

        public Builder<T> params(Map<String, String> params) {
            this.params = FcRequestBuildSupport.toMultiValueParams(params);
            return this;
        }

        public Builder<T> allParams(Map<String, List<String>> params) {
            this.params = FcRequestBuildSupport.sanitizeMultiValueParams(params);
            return this;
        }

        public Builder<T> method(FcRequestMethod requestMethod) {
            this.method = (Objects.isNull(requestMethod)) ? FcRequestMethod.GET : requestMethod;
            return this;
        }

        public Builder<T> header(String key, String value) {
            FcRequestBuildSupport.addSingleHeader(this.headers, key, value);
            return this;
        }

        public Builder<T> header(String key, List<String> value) {
            FcRequestBuildSupport.addMultiHeader(this.headers, key, value);
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
                headers.forEach(this::header);
            }
            return this;
        }

        public Builder<T> allHeaders(Map<String, List<String>> headers) {
            if (CollectionUtil.isNotEmpty(headers)) {
                headers.forEach(this::header);
            }
            return this;
        }

        public Builder<T> body(Object body) {
            if (Objects.isNull(body)) {
                return this;
            }
            return body(body, FcMediaType.APPLICATION_JSON);
        }

        public Builder<T> body(Object body, FcMediaType mediaType) {
            this.body = body;
            this.contentType = mediaType;
            return this;
        }

        public PreparedCall<T> prepared() {
            this.fullUrl = (StrUtil.isNotBlank(this.url)) ? this.url
                    : URLUtil.completeUrl(host, uri);
            this.fullUrl = FcRequestBuildSupport.appendQuery(this.fullUrl, params);

            return new PreparedCall<>(this.client, this);
        }

    }

    public static final class PreparedCall<T> {

        private final FastCallClient client;

        private final Builder<T> builder;

        private PreparedCall(FastCallClient client, Builder<T> builder) {
            this.client = client;
            this.builder = builder;
        }

        public FastCallResponse<T> callIt() {
            return this.client.callIt(builder, false);
        }

        public FastCallResponse<T> anonymousCallIt() {
            this.builder.callType = FcCallType.ANONYMOUS;
            return callIt();
        }

        public T call() {
            return this.client.callIt(builder, true).getData();
        }

        public T anonymousCall() {
            this.builder.callType = FcCallType.ANONYMOUS;
            return call();
        }
    }

}

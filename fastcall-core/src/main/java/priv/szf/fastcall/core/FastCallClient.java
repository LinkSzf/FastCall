package priv.szf.fastcall.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.Builder;
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
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.filter.FcFilterContext;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.model.FcApiPak;
import priv.szf.fastcall.common.model.FcApiParamPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.core.filter.FcFilterManager;
import priv.szf.fastcall.core.support.FcHttpRequestFactory;
import priv.szf.fastcall.core.support.FcHttpResponseMapper;
import priv.szf.fastcall.core.support.FcRequestBuildSupport;

import java.io.IOException;
import java.lang.reflect.Type;
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

    private final FcAuthType authType;

    private final IFcSource source;

    private final FcFilterManager filterManager;

    private <T> Request createRequest(Builder<T> builder) {
        Request request = FcHttpRequestFactory.createRequest(
                system,
                authType,
                builder.callType,
                builder.fullUrl,
                builder.method,
                builder.contentType,
                builder.body,
                builder.headers
        );
        return request.newBuilder()
                .tag(FastCallClient.class, this)
                .build();
    }

    private <T> FastCallResponse<T> buildStandardResponse(Builder<T> builder, Response response) {
        return FcHttpResponseMapper.buildStandardResponse(builder.dataType, builder.fullUrl, response);
    }

    private <T> FastCallResponse<T> doCall(Builder<T> builder, boolean throwException) {
        Request request = createRequest(builder);

        Call call = client.newCall(request);

        try (Response response = call.execute()) {
            return buildStandardResponse(builder, response);
        } catch (Exception e) {
            if (throwException) {
                throw new FcUnexpectedException(e, "Unexpected exception occurred when calling url[{}]", request.url());
            }
            return FastCallResponse.<T>builder()
                    .isSuccessful(false)
                    .isConnected(false)
                    .exception(e)
                    .build();
        }
    }

    public <T> Builder<T> newCall(Class<T> dataType) {
        return new Builder<>(this, dataType);
    }

    public <T> Builder<T> newCall(Type dataType) {
        return new Builder<>(this, dataType);
    }

    public <T> Builder<T> newCall() {
        return new Builder<>(this, Object.class);
    }

    public PreparedCall<Object> newApiCall(String apiName, FcApiParamPak params) {
        Map<String, FcApiPak> apiMap = source.getSourcePak(system).getApiMap();
        FcApiPak api = apiMap.get(apiName);
        if (Objects.isNull(api)) {
            throw new FastCallException("Source infos of api[{}] do not exist", apiName);
        }

        FcApiParamPak defaultParams = api.getParams();
        FcApiParamPak finalParams = (Objects.nonNull(params)) ? params
                : Optional.ofNullable(defaultParams).orElse(FcApiParamPak.empty());

        return newCall(Object.class)
                .apiName(apiName)
                .host(api.getParticularHost())
                .uri(api.getPath())
                .method(api.getMethod())
                .headers(finalParams.getHeaders())
                .params(finalParams.getParams())
                .body(finalParams.getBody())
                .prepared();
    }

    public PreparedCall<Object> newApiCall(String apiName) {
        return newApiCall(apiName, null);
    }

    private <T> FastCallResponse<T> callIt(Builder<T> builder, boolean throwException) {
        FcFilterContext context = createFilterContext(builder);
        AtomicReference<FastCallResponse<T>> responseRef = new AtomicReference<>();
        this.filterManager.doFilter(context, ctx -> {
            try {
                FastCallResponse<T> response = doCall(builder, throwException);
                responseRef.set(response);
                if (response.getCode() > 0) {
                    ctx.setResponseCode(response.getCode());
                }
                if (Objects.nonNull(response.getException())) {
                    ctx.setThrowable(response.getException());
                    ctx.setExceptionType(response.getException().getClass());
                }
                ctx.setSuccess(response.isSuccessful());
            }
            catch (Throwable throwable) {
                ctx.setThrowable(throwable);
                ctx.setExceptionType(throwable.getClass());
                ctx.setSuccess(false);
                throw throwable;
            }
        });
        return responseRef.get();
    }

    private <T> FcFilterContext createFilterContext(Builder<T> builder) {
        return FcFilterContext.builder()
                .system(system)
                .apiName(builder.apiName)
                .auth(builder.isAuth)
                .url(builder.fullUrl)
                .callType(builder.callType)
                .sourcePak(source.getSourcePak(system))
                .build();
    }

    public class Builder<T> {

        private final Map<String, List<String>> headers = new HashMap<>();

        private final FastCallClient client;

        private final Type dataType;

        private FcRequestMethod method = FcRequestMethod.GET;

        private FcMediaType contentType = FcMediaType.APPLICATION_JSON;

        private FcCallType callType = FcCallType.NORMAL;

        private boolean isAuth = false;

        private String apiName;

        private String host;

        private String uri;

        private String url;

        private String fullUrl;

        private Map<String, List<String>> params = new LinkedHashMap<>();

        private Object body;

        private Builder(FastCallClient client, Type dataType) {
            this.client = client;
            this.dataType = dataType;
            this.host = Optional.ofNullable(source)
                    .map(s -> s.getSourcePak(system))
                    .map(FcSourcePak::getSystem)
                    .map(FcSystemPak::getHost)
                    .orElse(null);
        }

        private Builder<T> apiName(String apiName) {
            this.apiName = apiName;
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
            this.uri = uri;
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

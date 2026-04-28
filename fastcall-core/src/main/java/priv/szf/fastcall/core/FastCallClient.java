package priv.szf.fastcall.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
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
import priv.szf.fastcall.common.event.request.IFcRequestEvent;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.model.FcApiPak;
import priv.szf.fastcall.common.model.FcApiParamPak;
import priv.szf.fastcall.core.event.FcApiRequestEvent;
import priv.szf.fastcall.core.event.FcAuthRequestEvent;
import priv.szf.fastcall.core.event.FcRequestEvent;
import priv.szf.fastcall.core.event.IFcRequestEventPublisher;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("all")
@Builder
public class FastCallClient {

    private final OkHttpClient client;

    private final String system;

    private final FcAuthType authType;

    private final IFcSource source;

    private final IFcRequestEventPublisher eventPublisher;

    private <T> Request createRequest(Builder<T> builder) {
        Headers.Builder headerBuilder = new Headers.Builder();
        builder.headers.forEach((key, values) ->
                values.forEach(value -> headerBuilder.add(key, value))
        );
        Headers headers = headerBuilder.build();

        FcRequestMethod method = builder.method;
        RequestBody requestBody = Optional.ofNullable(builder.body)
                .map(body -> {
                    if (body instanceof RequestBody) {
                        return (RequestBody) body;
                    }
                    MediaType mediaType = MediaType.parse(builder.contentType.getName());

                    if (body instanceof byte[]) {
                        return RequestBody.create((byte[]) body, mediaType);
                    }
                    if (body instanceof File) {
                        return RequestBody.create((File) body, mediaType);
                    }
                    String jsonStr = JSONUtil.toJsonStr(body);
                    return RequestBody.create(jsonStr, mediaType);
                })
                .orElse(null);

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
        String mediaType = Optional.ofNullable(body)
                .map(b -> b.contentType())
                .map(t -> t.toString())
                .orElse(null);
        Headers headers = response.headers();
        Map<String, List<String>> headersMap = headers.toMultimap();


        return FastCallResponse.<T>builder()
                .code(code)
                .message(message)
                .isSuccessful(isSuccessful)
                .data(data)
                .mediaType(mediaType)
                .headers(headersMap)
                .build();
    }

    private <T> FastCallResponse<T> doCall(Builder<T> builder) {
        Request request = createRequest(builder);

        Call call = client.newCall(request);

        try (Response response = call.execute()) {
            return buildStandardResponse(builder, response);
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "IO exception occured when exectuing url[{}]", builder.fullUrl);
        }
    }

    private <T> T readResponseBodyData(Type dataType, ResponseBody body, String url) {
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
                        "Response content type[{}] is not JSON, cannot convert to type[{}] in url[{}], body preview: {}",
                        Objects.toString(contentType, "null"),
                        dataType.getTypeName(),
                        url,
                        abbreviateBody(bodyStr)
                );
            }

            try {
                return JSONUtil.toBean(bodyStr, dataType, false);
            } catch (RuntimeException e) {
                throw new FcUnexpectedException(
                        e,
                        "JSON response cannot be converted to type[{}] in url[{}], body preview: {}",
                        dataType.getTypeName(),
                        url,
                        abbreviateBody(bodyStr)
                );
            }
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "IO exception occured when reading response body in url[{}]", url);
        } catch (ClassCastException e) {
            throw new FcUnexpectedException(e,
                    "Response body cannot be converted to type[{}] in url[{}]",
                    dataType.getTypeName(),
                    url);
        }
    }

    private boolean isJsonMediaType(MediaType mediaType) {
        return Objects.nonNull(mediaType) && StrUtil.containsIgnoreCase(mediaType.toString(), "json");
    }

    private String abbreviateBody(String bodyStr) {
        String value = StrUtil.nullToEmpty(bodyStr);
        int maxLen = 200;
        if (value.length() <= maxLen) {
            return value;
        }
        return value.substring(0, maxLen) + "...";
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

    private <T> FastCallResponse<T> callIt(Builder<T> builder) {
        IFcRequestEvent requestEvent = null;
        try {
            FastCallResponse<T> response = doCall(builder);
            requestEvent = createRequestEvent(builder, response);
            return response;
        }
        finally {
            if (Objects.isNull(requestEvent)) {
                requestEvent = new FcRequestEvent(builder.fullUrl, false);
            }
            eventPublisher.publish(requestEvent);
        }
    }

    private <T> IFcRequestEvent createRequestEvent(Builder<T> builder, FastCallResponse<T> response) {
        IFcRequestEvent requestEvent = null;

        String apiName = builder.apiName;
        if (Objects.nonNull(apiName)) {
            requestEvent = FcApiRequestEvent.builder()
                    .system(system)
                    .api(apiName)
                    .url(builder.fullUrl)
                    .success(response.isSuccessful())
                    .build();
        }
        else if (builder.isAuth) {
            requestEvent = FcAuthRequestEvent.builder()
                    .system(system)
                    .url(builder.fullUrl)
                    .success(response.isSuccessful())
                    .build();
        }
        else {
            requestEvent = new FcRequestEvent(builder.fullUrl, response.isSuccessful());
        }

        return requestEvent;
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
            this.host = source.getSourcePak(system).getSystem().getHost();
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
            this.params = new LinkedHashMap<>();
            if (CollectionUtil.isNotEmpty(params)) {
                params.forEach((k, v) -> {
                    if (Objects.nonNull(k) && Objects.nonNull(v)) {
                        List<String> values = this.params.computeIfAbsent(k, key -> new ArrayList<>());
                        values.add(v);
                    }
                });
            }
            return this;
        }

        public Builder<T> allParams(Map<String, List<String>> params) {
            this.params = new LinkedHashMap<>();
            if (CollectionUtil.isNotEmpty(params)) {
                params.forEach((k, vs) -> {
                    if (Objects.nonNull(k) && CollectionUtil.isNotEmpty(vs)) {
                        List<String> values = this.params.computeIfAbsent(k, key -> new ArrayList<>());
                        vs.stream()
                                .filter(Objects::nonNull)
                                .forEach(values::add);
                    }
                });
            }
            return this;
        }

        public Builder<T> method(FcRequestMethod requestMethod) {
            this.method = (Objects.isNull(requestMethod)) ? FcRequestMethod.GET : requestMethod;
            return this;
        }

        public Builder<T> header(String key, String value) {
            List<String> values = this.headers.computeIfAbsent(key, k -> new ArrayList<>());
            values.add(value);
            return this;
        }

        public Builder<T> header(String key, List<String> value) {
            List<String> values = this.headers.computeIfAbsent(key, k -> new ArrayList<>());
            values.addAll(value);
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

            if (CollectionUtil.isNotEmpty(params)) {
                String query = buildMultiValueQuery(params);
                if (StrUtil.isNotBlank(query)) {
                    if (StrUtil.contains(this.fullUrl, "?")) {
                        if (StrUtil.endWithAny(this.fullUrl, "?", "&")) {
                            this.fullUrl = this.fullUrl + query;
                        } else {
                            this.fullUrl = this.fullUrl + "&" + query;
                        }
                    } else {
                        this.fullUrl = this.fullUrl + "?" + query;
                    }
                }
            }

            return new PreparedCall<>(this.client, this);
        }

        private String buildMultiValueQuery(Map<String, List<String>> params) {
            StringBuilder queryBuilder = new StringBuilder();
            params.forEach((k, values) -> {
                if (CollectionUtil.isEmpty(values)) {
                    return;
                }
                for (String value : values) {
                    if (queryBuilder.length() > 0) {
                        queryBuilder.append("&");
                    }
                    String encodedKey = URLUtil.encode(k, StandardCharsets.UTF_8);
                    String encodedValue = (value == null) ? "" : URLUtil.encode(value, StandardCharsets.UTF_8);
                    queryBuilder.append(encodedKey).append("=").append(encodedValue);
                }
            });
            return queryBuilder.toString();
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
            return client.callIt(builder);
        }

        public FastCallResponse<T> anonymousCallIt() {
            builder.callType = FcCallType.ANONYMOUS;
            return callIt();
        }

        public T call() {
            return callIt().getData();
        }

        public T anonymousCall() {
            return anonymousCallIt().getData();
        }
    }

}

package priv.szf.fastcall.core.declarative;

import cn.hutool.core.util.StrUtil;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.FastCall;
import priv.szf.fastcall.core.FastCallClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

final class FcClientInvocationHandler implements InvocationHandler {

    private final Class<?> interfaceType;

    private final FastCall fastCall;

    private final Map<Method, FcClientMethodMetadata> methodMetadataMap;

    private final FcClientRequestResolver requestResolver;

    FcClientInvocationHandler(Class<?> interfaceType, FastCall fastCall, IFcSource source) {
        this.interfaceType = interfaceType;
        this.fastCall = fastCall;
        this.methodMetadataMap = new FcClientMetadataParser(interfaceType).parse();
        this.requestResolver = new FcClientRequestResolver(source);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return invokeObjectMethod(proxy, method, args);
        }
        if (method.isDefault()) {
            throw new UnsupportedOperationException("Default methods are not supported in FastCall declarative client");
        }

        FcClientMethodMetadata metadata = methodMetadataMap.get(method);
        if (Objects.isNull(metadata)) {
            throw new FastCallException("No method metadata found for [{}#{}]", interfaceType.getName(), method.getName());
        }

        FcResolvedRequest request = requestResolver.resolve(metadata, args);
        FastCallClient.PreparedCall<?> preparedCall = buildCallBuilder(request, metadata.getDataType()).prepared();

        if (metadata.isReturnResponse()) {
            return metadata.isAnonymous() ? preparedCall.anonymousCallIt() : preparedCall.callIt();
        }

        if (metadata.isReturnVoid()) {
            if (metadata.isAnonymous()) {
                preparedCall.anonymousCallIt();
            } else {
                preparedCall.callIt();
            }
            return null;
        }

        return metadata.isAnonymous() ? preparedCall.anonymousCall() : preparedCall.call();
    }

    private FastCallClient.Builder<?> buildCallBuilder(FcResolvedRequest request, Type dataType) {
        FastCallClient client = fastCall.getClient(request.getSystem());
        FastCallClient.Builder<?> builder = client.newCall(dataType)
                .method(request.getMethod())
                .uri(request.getUri())
                .headers(request.getHeaders())
                .params(request.getQueries());

        if (StrUtil.isNotBlank(request.getHost())) {
            builder.host(request.getHost());
        }

        if (request.isHasBody()) {
            builder.body(request.getBody(), request.getBodyType());
        }
        return builder;
    }

    private Object invokeObjectMethod(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        if ("toString".equals(methodName)) {
            return "FastCallDeclarativeClient(" + interfaceType.getName() + ")";
        }
        if ("hashCode".equals(methodName)) {
            return System.identityHashCode(proxy);
        }
        if ("equals".equals(methodName)) {
            return Objects.nonNull(args) && args.length == 1 && proxy == args[0];
        }
        throw new UnsupportedOperationException("Unsupported Object method: " + methodName);
    }
}

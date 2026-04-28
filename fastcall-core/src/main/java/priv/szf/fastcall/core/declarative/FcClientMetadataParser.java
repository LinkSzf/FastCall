package priv.szf.fastcall.core.declarative;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.declarative.annotation.FcBody;
import priv.szf.fastcall.core.declarative.annotation.FcClient;
import priv.szf.fastcall.core.declarative.annotation.FcHeader;
import priv.szf.fastcall.core.declarative.annotation.FcMethod;
import priv.szf.fastcall.core.declarative.annotation.FcPath;
import priv.szf.fastcall.core.declarative.annotation.FcQuery;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
final class FcClientMetadataParser {

    private final Class<?> interfaceType;

    Map<Method, FcClientMethodMetadata> parse() {
        FcClient clientAnno = Optional.ofNullable(interfaceType.getAnnotation(FcClient.class))
                .orElseThrow(() -> new FastCallException("Interface[{}] must be annotated with @FcClient", interfaceType.getName()));

        String interfaceSystem = clientAnno.system();
        if (StrUtil.isBlank(interfaceSystem)) {
            throw new FastCallException("Interface[{}] @FcClient.system cannot be blank", interfaceType.getName());
        }

        Method[] methods = interfaceType.getMethods();
        Map<Method, FcClientMethodMetadata> metadataMap = new HashMap<>();
        for (Method method : methods) {
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            if (method.isDefault() || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            FcClientMethodMetadata metadata = parseMethodMetadata(interfaceSystem, method);
            metadataMap.put(method, metadata);
        }
        return Collections.unmodifiableMap(metadataMap);
    }

    private FcClientMethodMetadata parseMethodMetadata(String interfaceSystem, Method method) {
        FcMethod methodAnno = Optional.ofNullable(method.getAnnotation(FcMethod.class))
                .orElseThrow(() -> new FastCallException(
                        "Method[{}#{}] must be annotated with @FcMethod", interfaceType.getName(), method.getName()
                ));

        String system = StrUtil.blankToDefault(methodAnno.system(), interfaceSystem);
        String api = methodAnno.api();
        String uri = methodAnno.uri();
        boolean hasApi = StrUtil.isNotBlank(api);
        boolean hasUri = StrUtil.isNotBlank(uri);
        if (hasApi == hasUri) {
            throw new FastCallException(
                    "Method[{}#{}] must specify exactly one of @FcMethod.api or @FcMethod.uri",
                    interfaceType.getName(), method.getName()
            );
        }

        ReturnInfo returnInfo = parseReturnInfo(method);
        List<FcClientMethodMetadata.ParamBinding> paramBindings = parseParamBindings(method);

        return new FcClientMethodMetadata(
                method,
                system,
                api,
                uri,
                methodAnno.method(),
                methodAnno.host(),
                methodAnno.anonymous(),
                methodAnno.bodyType(),
                returnInfo.dataType,
                returnInfo.returnResponse,
                returnInfo.returnVoid,
                paramBindings
        );
    }

    private ReturnInfo parseReturnInfo(Method method) {
        Class<?> returnType = method.getReturnType();
        boolean returnResponse = FastCallResponse.class.isAssignableFrom(returnType);
        boolean returnVoid = returnType == Void.TYPE || returnType == Void.class;
        Type genericReturnType = method.getGenericReturnType();

        Type dataType;
        if (returnResponse) {
            dataType = resolveResponseDataType(genericReturnType);
        } else if (returnVoid) {
            dataType = Object.class;
        } else {
            dataType = normalizeType(genericReturnType);
        }
        return new ReturnInfo(dataType, returnResponse, returnVoid);
    }

    private Type resolveResponseDataType(Type genericReturnType) {
        if (!(genericReturnType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] args = ((ParameterizedType) genericReturnType).getActualTypeArguments();
        if (args.length == 0) {
            return Object.class;
        }
        return normalizeType(args[0]);
    }

    private Type normalizeType(Type type) {
        if (type instanceof Class || type instanceof ParameterizedType) {
            return type;
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if (upperBounds.length > 0) {
                return normalizeType(upperBounds[0]);
            }
            return Object.class;
        }
        if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            if (bounds.length > 0) {
                return normalizeType(bounds[0]);
            }
            return Object.class;
        }
        return Object.class;
    }

    private List<FcClientMethodMetadata.ParamBinding> parseParamBindings(Method method) {
        Annotation[][] allParamAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<FcClientMethodMetadata.ParamBinding> bindings = new ArrayList<>();
        int bodyCount = 0;

        for (int i = 0; i < allParamAnnotations.length; i++) {
            Annotation[] annotations = allParamAnnotations[i];
            FcClientMethodMetadata.ParamBinding binding = parseSingleParamBinding(method, i, annotations, parameterTypes[i]);
            bindings.add(binding);
            if (binding.getKind() == FcClientMethodMetadata.ParamKind.BODY) {
                bodyCount++;
            }
        }

        if (bodyCount > 1) {
            throw new FastCallException("Method[{}#{}] can only have one @FcBody parameter", interfaceType.getName(), method.getName());
        }
        return bindings;
    }

    private FcClientMethodMetadata.ParamBinding parseSingleParamBinding(
            Method method,
            int index,
            Annotation[] annotations,
            Class<?> parameterType
    ) {
        FcClientMethodMetadata.ParamBinding binding = null;
        for (Annotation annotation : annotations) {
            FcClientMethodMetadata.ParamBinding current = null;

            if (annotation instanceof FcQuery) {
                String queryKey = ((FcQuery) annotation).value();
                validateKvBinding(method, index, queryKey, parameterType, "@FcQuery");
                current = new FcClientMethodMetadata.ParamBinding(
                        FcClientMethodMetadata.ParamKind.QUERY,
                        index,
                        queryKey,
                        null
                );
            } else if (annotation instanceof FcHeader) {
                String headerKey = ((FcHeader) annotation).value();
                validateKvBinding(method, index, headerKey, parameterType, "@FcHeader");
                current = new FcClientMethodMetadata.ParamBinding(
                        FcClientMethodMetadata.ParamKind.HEADER,
                        index,
                        headerKey,
                        null
                );
            } else if (annotation instanceof FcPath) {
                String pathName = ((FcPath) annotation).value();
                if (StrUtil.isBlank(pathName)) {
                    throw new FastCallException("Method[{}#{}] @FcPath value cannot be blank", interfaceType.getName(), method.getName());
                }
                current = new FcClientMethodMetadata.ParamBinding(
                        FcClientMethodMetadata.ParamKind.PATH,
                        index,
                        pathName,
                        null
                );
            } else if (annotation instanceof FcBody) {
                current = new FcClientMethodMetadata.ParamBinding(
                        FcClientMethodMetadata.ParamKind.BODY,
                        index,
                        null,
                        ((FcBody) annotation).mediaType()
                );
            }

            if (Objects.nonNull(current)) {
                if (Objects.nonNull(binding)) {
                    throw new FastCallException(
                            "Method[{}#{}] parameter[{}] contains multiple FastCall parameter annotations",
                            interfaceType.getName(), method.getName(), index
                    );
                }
                binding = current;
            }
        }

        if (Objects.isNull(binding)) {
            throw new FastCallException(
                    "Method[{}#{}] parameter[{}] must be annotated with one of @FcQuery/@FcHeader/@FcPath/@FcBody",
                    interfaceType.getName(), method.getName(), index
            );
        }
        return binding;
    }

    private void validateKvBinding(
            Method method,
            int index,
            String key,
            Class<?> parameterType,
            String annotationName
    ) {
        boolean mapType = Map.class.isAssignableFrom(parameterType);
        if (StrUtil.isBlank(key) && !mapType) {
            throw new FastCallException(
                    "Method[{}#{}] parameter[{}] {} key is blank, so argument type must be Map",
                    interfaceType.getName(), method.getName(), index, annotationName
            );
        }
        if (StrUtil.isNotBlank(key) && mapType) {
            throw new FastCallException(
                    "Method[{}#{}] parameter[{}] {} key is non-blank, so argument type cannot be Map",
                    interfaceType.getName(), method.getName(), index, annotationName
            );
        }
    }

    @RequiredArgsConstructor
    private static final class ReturnInfo {
        private final Type dataType;
        private final boolean returnResponse;
        private final boolean returnVoid;
    }
}

package priv.szf.fastcall.core.declarative;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

@Getter
@RequiredArgsConstructor
final class FcClientMethodMetadata {

    private final Method method;

    private final String system;

    private final String apiName;

    private final String uri;

    private final FcRequestMethod requestMethod;

    private final String host;

    private final boolean anonymous;

    private final FcMediaType defaultBodyType;

    private final Type dataType;

    private final boolean returnResponse;

    private final boolean returnVoid;

    private final List<ParamBinding> paramBindings;

    @Getter
    @RequiredArgsConstructor
    static final class ParamBinding {

        private final ParamKind kind;

        private final int index;

        private final String name;

        private final FcMediaType bodyMediaType;
    }

    enum ParamKind {
        QUERY,
        HEADER,
        PATH,
        BODY
    }
}

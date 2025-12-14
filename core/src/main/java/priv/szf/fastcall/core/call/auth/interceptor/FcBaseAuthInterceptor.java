package priv.szf.fastcall.core.call.auth.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import priv.szf.fastcall.core.call.auth.FcCallType;

public abstract class FcBaseAuthInterceptor implements Interceptor {

    boolean isNotAuthNeed(Request originRequest) {
        FcCallType callType = originRequest.tag(FcCallType.class);
        return callType == FcCallType.ANONYMOUS;
    }
}

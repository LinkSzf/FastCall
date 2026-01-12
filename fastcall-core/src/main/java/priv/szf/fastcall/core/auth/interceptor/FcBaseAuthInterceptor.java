package priv.szf.fastcall.core.auth.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.auth.IFcAuthHandler;

import java.io.IOException;

public abstract class FcBaseAuthInterceptor implements Interceptor {

    protected abstract IFcAuthHandler getAuthHandler();

    protected abstract Response doAfterProceed(Chain chain, Request request, Response response) throws IOException;
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        if (isNotAuthNeed(originRequest)
                || shouldNotIntercept(originRequest)
        ) {
            return chain.proceed(originRequest);
        }

        Request modifiedRequest = modifyRequest(originRequest);

        Response response = chain.proceed(modifiedRequest);

        return doAfterProceed(chain, modifiedRequest, response);
    }

    protected Request modifyRequest(Request request) {
        return getAuthHandler().modifyRequest(request);
    }

    protected boolean shouldNotIntercept(Request request) {
        return false;
    }

    protected boolean isNotAuthNeed(Request request) {
        return getAuthHandler().isNotAuthNeed(request);
    }
}

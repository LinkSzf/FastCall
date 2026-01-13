package priv.szf.fastcall.core.auth.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.auth.IFcAuthHandler;

import java.io.IOException;

public abstract class FcBaseAuthInterceptor implements Interceptor {

    protected abstract IFcAuthHandler getAuthHandler();

    protected abstract boolean shouldSkip(Request request);

    protected abstract Request doBeforeProceed(Request request);

    protected abstract Response doAfterProceed(Chain chain, Request request, Response response) throws IOException;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        if (shouldSkip(originRequest)) {
            return chain.proceed(originRequest);
        }

        Request request = doBeforeProceed(originRequest);

        Response response = chain.proceed(request);

        return doAfterProceed(chain, request, response);
    }

    protected Request modifyRequest(Request request) {
        return getAuthHandler().modifyRequest(request);
    }

}

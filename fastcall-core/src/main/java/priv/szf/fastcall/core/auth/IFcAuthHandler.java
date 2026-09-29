package priv.szf.fastcall.core.auth;

import okhttp3.Request;
import priv.szf.fastcall.common.FcAuthType;

/**
 * Applies the credential of one {@code FcAuthType} to the outgoing request.
 * Implementations are Spring beans and may replace the built-in handlers.
 */
public interface IFcAuthHandler {

    FcAuthType getAuthType();

    Request modifyRequest(Request request, FcRequestContext context);


}

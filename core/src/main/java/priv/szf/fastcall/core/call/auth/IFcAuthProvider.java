package priv.szf.fastcall.core.call.auth;

import okhttp3.Request;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.model.FcTokenPak;

public interface IFcAuthProvider<C, T> {

    Object getRequestBody(C authContent);

    FcTokenPak<T> mapToToken(Request request, FastCallResponse<String> response, C authContent);
}

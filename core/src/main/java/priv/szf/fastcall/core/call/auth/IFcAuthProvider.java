package priv.szf.fastcall.core.call.auth;

import okhttp3.Request;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.model.FcTokenPak;

public interface IFcAuthProvider<T> {

    Object getRequestBody(T authContent);

    FcTokenPak mapToToken(Request request, FastCallResponse<String> response, T authContent);
}

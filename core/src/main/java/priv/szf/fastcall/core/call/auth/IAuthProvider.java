package priv.szf.fastcall.core.call.auth;

import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.model.FcTokenPak;

public interface IAuthProvider<T> {

    Object getRequestBody(T authContent);

    FcTokenPak mapToToken(FastCallResponse<String> response, T authContent);
}

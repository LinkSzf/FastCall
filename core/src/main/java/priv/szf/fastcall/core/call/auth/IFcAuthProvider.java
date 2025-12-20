package priv.szf.fastcall.core.call.auth;

import okhttp3.Request;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

public interface IFcAuthProvider<C extends BaseAuthContent, T> {

    FcTokenPak<T> mapToToken(Request request, FastCallResponse<String> response, C authContent);
}

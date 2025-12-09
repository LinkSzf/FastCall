package priv.szf.fastcall.core.call.auth;

import priv.szf.fastcall.core.model.FcTokenPak;

import java.util.Map;

public interface IAuthProvider<T> {

    Object getRequestBody(T authContent);
    Map<String, String> getResponseMap(T authContent);

    FcTokenPak mapToToken(Map<String, Object> resultMap);
}

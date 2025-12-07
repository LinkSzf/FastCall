package priv.szf.fastcall.core.call.auth;

import java.util.Map;

public interface IRefreshableAuth {

    Map<String, String> getParams();

    String getTokenField();

}

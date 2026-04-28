package priv.szf.fastcall.test.client;

import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.declarative.annotation.FcBody;
import priv.szf.fastcall.core.declarative.annotation.FcClient;
import priv.szf.fastcall.core.declarative.annotation.FcHeader;
import priv.szf.fastcall.core.declarative.annotation.FcMethod;
import priv.szf.fastcall.core.declarative.annotation.FcPath;
import priv.szf.fastcall.core.declarative.annotation.FcQuery;

import java.util.Map;

@FcClient(system = "declarative-system")
public interface DeclarativeDemoClient {

    @FcMethod(uri = "/declarative/target/echo/{id}", method = FcRequestMethod.GET)
    String echo(@FcPath("id") String id,
                @FcQuery("q") String q,
                @FcHeader("X-Req-Id") String reqId
    );

    @FcMethod(uri = "/declarative/target/payload", method = FcRequestMethod.POST)
    FastCallResponse<Map<String, Object>> postPayload(@FcBody Map<String, Object> payload);

    @FcMethod(uri = "/declarative/target/anonymous", method = FcRequestMethod.GET, anonymous = true)
    String anonymous(@FcQuery("name") String name);
}


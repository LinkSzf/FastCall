package priv.szf.fastcall.core.call.auth.handler;

import okhttp3.Credentials;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.auth.BasicAuth;

@Component
public class FcBasicAuthHandler extends FcBaseAuthHandler<BasicAuth> {

    public final

    @Override
    AuthType getAuthType() {
        return AuthType.BASIC;
    }

    @Override
    public Request modifyRequest(Request request) {
        BasicAuth content = getAuthContent(request);
        String authorization = genAuthorization(content);
        return request.newBuilder()
                .header("Authorization", authorization)
                .build();
    }

    private String genAuthorization (BasicAuth content) {
        return Credentials.basic(content.getUsername(), content.getPassword());
    }
}

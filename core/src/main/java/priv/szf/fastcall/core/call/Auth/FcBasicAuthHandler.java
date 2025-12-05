package priv.szf.fastcall.core.call.Auth;

import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.auth.BasicAuth;

import java.util.Base64;

@Component
public class FcBasicAuthHandler extends FcBaseAuthHandler<BasicAuth> {

    public final

    @Override
    AuthType getAuthType() {
        return AuthType.BASIC;
    }

    @Override
    public Request modifyRequest(Request request) {
        BasicAuth content = getContent(request);
        String authorization = genAuthorization(content);

        return request.newBuilder()
                .header("Authorization", authorization)
                .build();
    }

    private String genAuthorization (BasicAuth content) {
        String credentials = content.getUsername() + ":" + content.getPassword();
        return getAuthType().getPrefix() + Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}

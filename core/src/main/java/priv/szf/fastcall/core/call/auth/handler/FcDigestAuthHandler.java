package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.call.auth.provider.FcDigestAuthProvider;
import priv.szf.fastcall.core.call.auth.provider.digest.ClientNonceMagnager;
import priv.szf.fastcall.core.call.auth.provider.digest.DigestCredential;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.DigestAuth;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class FcDigestAuthHandler extends FcBaseTokenAuthHandler<DigestAuth, DigestCredential> {

    private final IFcSource source;

    @Override
    public AuthType getAuthType() {
        return AuthType.DIGEST;
    }

    @Override
    protected IFcSource getSource() {
        return source;
    }

    @Override
    public boolean isAuthPreRefreshable() {
        return false;
    }

    @Override
    public boolean isInvalidToken(FcTokenPak<DigestCredential> accessToken) {
        return true;
    }

    @Override
    public IFcAuthProvider<DigestAuth, DigestCredential> getAuthProvider(String systemCode) {
        return new FcDigestAuthProvider(systemCode);
    }

    @Override
    protected FcTokenPak<DigestCredential> callAndGetToken(Request request, Response response, String systemCode) {
        IFcAuthProvider<DigestAuth, DigestCredential> authProvider = getAuthProvider(systemCode);

        Map<String, List<String>> headers = response.headers().toMultimap();
        FastCallResponse<String> authResponse = FastCallResponse.<String>builder()
                .code(response.code())
                .message(response.message())
                .isSuccessful(response.isSuccessful())
                .headers(headers)
                .build();

        DigestAuth authContent = getAuthContent(request);
        return authProvider.mapToToken(request, authResponse, authContent);
    }

    @Override
    public Request modifyRequest(Request request) {
        DigestCredential credential = getToken(request);
        if (Objects.isNull(credential)) {
            return request;
        }

        String systemCode = getSystemCode(request);
        int nextNc = ClientNonceMagnager.system(systemCode).getNextNc(credential.getNonce());
        String token = credential.buildCredentialStr();

        String authorization = getAuthType().getPrefix() + token;
        return request.newBuilder()
                .header(FcHttpHeader.AUTHORIZATION.getName(), authorization)
                .build();
    }
}

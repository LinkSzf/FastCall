package priv.szf.fastcall.core.call.auth.provider;

import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.FcUtils;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.call.auth.provider.digest.ClientNonceMagnager;
import priv.szf.fastcall.core.call.auth.provider.digest.DigestChallenge;
import priv.szf.fastcall.core.call.auth.provider.digest.DigestCredential;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.DigestAuth;

import java.time.LocalDateTime;

public class FcDigestAuthProvider extends FcDefaultFcAuthProvider<DigestAuth> {

    public FcDigestAuthProvider(String identity) {
        super(identity);
    }

    @Override
    public FcTokenPak mapToToken(Request request, FastCallResponse<String> response, DigestAuth authContent) {
        super.checkSuccess(response);

        String authenticateHeader = response.getHeader(FcHttpHeader.WWW_AUTHENTICATE.getName());
        if (StringUtils.startsWith(authenticateHeader, "Digest")) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，未识别到Digest认证头，code[%s], message[%s], data[%s]",
                            getIdentity(), response.getCode(), response.getMessage(), response.getData())
            );
        }

        DigestChallenge challenge = DigestChallenge.parse(authenticateHeader);
        int nextNc = ClientNonceMagnager.system(getIdentity()).getNextNc(challenge.getNonce());
        String method = request.method();
        String uri = request.url().encodedPath();
        byte[] body = FcUtils.readRequestBody(request);
        DigestCredential credentials = DigestCredential.builder()
                .username(authContent.getUsername())
                .password(authContent.getPassword())
                .challenge(challenge)
                .uri(uri)
                .method(method)
                .body(body)
                .nc(nextNc)
                .build();

        String authHeader = credentials.buildCredentialStr();

        return FcTokenPak.builder()
                .token(authHeader)
                .issuance(LocalDateTime.now())
                .estimatedExpiration(LocalDateTime.MAX)
                .build();
    }

}

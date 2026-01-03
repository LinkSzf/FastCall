package priv.szf.fastcall.core.call.auth.provider;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.FcUtils;
import priv.szf.fastcall.core.call.auth.IFcInteractiveAuthProvider;
import priv.szf.fastcall.core.call.auth.provider.digest.ClientNonceManager;
import priv.szf.fastcall.core.call.auth.provider.digest.DigestChallenge;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.exception.FastCallException;
import priv.szf.fastcall.core.common.exception.FcUnexpectedException;
import priv.szf.fastcall.core.model.auth.DigestAuthContent;
import priv.szf.fastcall.core.model.auth.credential.DigestCredential;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class FcDigestAuthProvider extends FcBaseInteractiveAuthProvider<DigestAuthContent>
    implements IFcInteractiveAuthProvider<DigestAuthContent> {

    private final Map<String, ClientNonceManager> systemNonceManagerMap = new ConcurrentHashMap<>();

    private final IFcSource source;

    @Override
    protected IFcSource getSource() {
        return source;
    }

    @Override
    protected DigestCredential buildCredential(DigestAuthContent authContent,
                                               Request request,
                                               Response response,
                                               String system) {
        String authenticateHeader = response.header(FcHttpHeader.WWW_AUTHENTICATE.getName());
        if (!StringUtils.startsWith(authenticateHeader, AuthType.DIGEST.getPrefix())) {
            throw new FastCallException(
                            "未识别到Digest认证头，code[%s], message[%s], data[%s]",
                            response.code(), response.message(), response.body()
            );
        }

        DigestChallenge challenge = DigestChallenge.parse(authenticateHeader);
        String method = request.method();
        String uri = request.url().encodedPath();
        byte[] body = FcUtils.readRequestBody(request);
        return DigestCredential.builder()
                .username(authContent.getUsername())
                .password(authContent.getPassword())
                .challenge(challenge)
                .uri(uri)
                .method(method)
                .body(body)
                .system(system)
                .nonceManager(systemNonceManagerMap.computeIfAbsent(system, v -> new ClientNonceManager()))
                .build();
    }

}

package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.FcUtils;
import priv.szf.fastcall.core.auth.IFcDynAuthProvider;
import priv.szf.fastcall.core.auth.provider.digest.ClientNonceManager;
import priv.szf.fastcall.core.auth.provider.digest.DigestChallenge;
import priv.szf.fastcall.core.source.IFcSource;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.DigestAuthContent;
import priv.szf.fastcall.core.model.credential.DigestCredential;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class FcDigestAuthProvider extends FcBaseDynAuthProvider<DigestAuthContent>
    implements IFcDynAuthProvider<DigestAuthContent> {

    private final Map<String, ClientNonceManager> systemNonceManagerMap = new ConcurrentHashMap<>();

    private final IFcSource source;

    @Override
    protected IFcSource getSource() {
        return source;
    }

    @Override
    protected DigestCredential buildCredential(@NonNull DigestAuthContent authContent,
                                               @NonNull Request request,
                                               @NonNull Response response,
                                               @NonNull String system
    ) {
        String authenticateHeader = response.header(FcHttpHeader.WWW_AUTHENTICATE.getName());
        if (!StringUtils.startsWith(authenticateHeader, FcAuthType.DIGEST.getPrefix())) {
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

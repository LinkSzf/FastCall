package priv.szf.fastcall.core.auth.provider;

import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.core.FcUtils;
import priv.szf.fastcall.core.auth.IFcDynAuthProvider;
import priv.szf.fastcall.core.auth.provider.digest.ClientNonceManager;
import priv.szf.fastcall.core.auth.provider.digest.DigestChallenge;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.model.content.DigestAuthContent;
import priv.szf.fastcall.core.model.credential.DigestCredential;
import priv.szf.fastcall.core.source.FcSourceDelegate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class FcDigestAuthProvider extends FcBaseDynAuthProvider<DigestAuthContent>
    implements IFcDynAuthProvider {

    private final Map<String, ClientNonceManager> systemNonceManagerMap = new ConcurrentHashMap<>();

    private final FcSourceDelegate source;

    @Override
    protected IFcSource getSource() {
        return source;
    }

    public void removeNonceManager(String system) {
        if (system != null) {
            systemNonceManagerMap.remove(system);
        }
    }

    @Override
    protected DigestCredential buildCredential(@NonNull DigestAuthContent authContent,
                                               @NonNull Request request,
                                               @NonNull Response response,
                                               @NonNull String system
    ) {
        String authenticateHeader = response.header(FcHttpHeader.WWW_AUTHENTICATE.getName());
        if (!StrUtil.startWithIgnoreCase(authenticateHeader, FcAuthType.DIGEST.getPrefix())) {
            throw new FcUnexpectedException(
                            "Digest auth header not recognized, code[{}], message[{}], data[{}]",
                            response.code(), response.message(), response.body()
            );
        }

        DigestChallenge challenge = DigestChallenge.parse(authenticateHeader);
        String method = request.method();
        String uri = request.url().encodedPath();
        byte[] body = FcUtils.readRequestBody(request);
        FcUtils.cacheRequestBodySnapshot(request, body);
        return DigestCredential.builder()
                .username(authContent.getUsername())
                .password(authContent.getPassword())
                .challenge(challenge)
                .uri(uri)
                .method(method)
                .body(body)
                .nonceManager(systemNonceManagerMap.computeIfAbsent(system, v -> new ClientNonceManager()))
                .build();
    }

    @Override
    protected ICredential buildCredential(@NotNull DigestAuthContent authContent) {
        return null;
    }

}

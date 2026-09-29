//package io.github.linkszf.fastcall.core.auth.provider;
//
//import cn.hutool.core.util.StrUtil;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.stereotype.Component;
//import io.github.linkszf.fastcall.common.exception.FcUnexpectedException;
//import io.github.linkszf.fastcall.common.model.credential.ICredential;
//import io.github.linkszf.fastcall.core.FcUtils;
//import io.github.linkszf.fastcall.core.auth.FcRequestContext;
//import io.github.linkszf.fastcall.core.auth.IFcDynAuthProvider;
//import io.github.linkszf.fastcall.core.auth.provider.digest.ClientNonceManager;
//import io.github.linkszf.fastcall.core.auth.provider.digest.DigestChallenge;
//import io.github.linkszf.fastcall.common.source.IFcSource;
//import io.github.linkszf.fastcall.common.FcAuthType;
//import io.github.linkszf.fastcall.common.FcHttpHeader;
//import io.github.linkszf.fastcall.common.model.content.DigestAuthContent;
//import io.github.linkszf.fastcall.core.model.credential.DigestCredential;
//import io.github.linkszf.fastcall.core.source.FcSourceDelegate;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@RequiredArgsConstructor
//@Component
//public class FcDigestAuthProvider extends FcBaseDynAuthProvider<DigestAuthContent>
//    implements IFcDynAuthProvider {
//
//    private final Map<String, ClientNonceManager> systemNonceManagerMap = new ConcurrentHashMap<>();
//
//    private final FcSourceDelegate source;
//
//    @Override
//    protected IFcSource getSource() {
//        return source;
//    }
//
//    public void removeNonceManager(String system) {
//        if (system != null) {
//            systemNonceManagerMap.remove(system);
//        }
//    }
//
//    @Override
//    protected DigestCredential buildCredential(@NonNull DigestAuthContent authContent, FcRequestContext context
//    ) {
//        String authenticateHeader = response.header(FcHttpHeader.WWW_AUTHENTICATE.getName());
//        if (!StrUtil.startWithIgnoreCase(authenticateHeader, FcAuthType.DIGEST.getPrefix())) {
//            throw new FcUnexpectedException(
//                            "Digest auth header not recognized, code[{}], message[{}], data[{}]",
//                            response.code(), response.message(), response.body()
//            );
//        }
//
//        DigestChallenge challenge = DigestChallenge.parse(authenticateHeader);
//        String method = request.method();
//        String uri = request.url().encodedPath();
//        byte[] body = FcUtils.readRequestBody(request);
//        FcUtils.cacheRequestBodySnapshot(request, body);
//        return DigestCredential.builder()
//                .username(authContent.getUsername())
//                .password(authContent.getPassword())
//                .challenge(challenge)
//                .uri(uri)
//                .method(method)
//                .body(body)
//                .nonceManager(systemNonceManagerMap.computeIfAbsent(system, v -> new ClientNonceManager()))
//                .build();
//    }
//
//    @Override
//    protected ICredential buildCredential(@NotNull DigestAuthContent authContent) {
//        return null;
//    }
//
//}

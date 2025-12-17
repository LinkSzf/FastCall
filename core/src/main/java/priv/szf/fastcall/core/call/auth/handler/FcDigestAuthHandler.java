package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.call.auth.provider.FcDigestAuthProvider;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.DigestAuth;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FcDigestAuthHandler extends FcBaseTokenAuthHandler<DigestAuth> {

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
    public boolean isInvalidToken(FcTokenPak accessToken) {
        return true;
    }

    @Override
    public IFcAuthProvider<DigestAuth> getAuthProvider(String systemCode) {
        return new FcDigestAuthProvider(systemCode);
    }

    @Override
    protected FcTokenPak callAndGetToken(Request request, Response response, String systemCode) {
        IFcAuthProvider<DigestAuth> authProvider = getAuthProvider(systemCode);

//        FastCallClient client = FastCallClientFactory.getExistedClient(systemCode);
//        FcSourcePak sourceInfo = getSourceInfo(request);
        DigestAuth authContent = getAuthContent(request);
//        FastCallResponse<String> authResponse = client.newCall(String.class)
//                .url(getUrl(sourceInfo))
//                .method(FcRequestMethod.POST)
//                .header(FcHttpHeader.CONTENT_TYPE, FcMediaType.APPLICATION_JSON)
//                .header(FcHttpHeader.ACCEPT, FcMediaType.APPLICATION_JSON)
//                .body(authProvider.getRequestBody(authContent))
//                .prepared()
//                .anonymousCall();

//        String authenticateHeader = request.header(FcHttpHeader.WWW_AUTHENTICATE.getName());
//        if (StringUtils.startsWith(authenticateHeader, getAuthType().getPrefix())) {
//            throw new FcUnexpectedException(
//                    String.format("FastCall-系统[%s]刷新认证失败，未识别到Digest认证头，code[%s], message[%s]",
//                            systemCode, response.code(), response.message())
//            );
//        }


        Map<String, List<String>> headers = response.headers().toMultimap();
        FastCallResponse<String> authResponse = FastCallResponse.<String>builder()
                .code(response.code())
                .message(response.message())
                .isSuccessful(response.isSuccessful())
                .headers(headers)
                .build();


        return authProvider.mapToToken(authResponse, authContent);
    }
}

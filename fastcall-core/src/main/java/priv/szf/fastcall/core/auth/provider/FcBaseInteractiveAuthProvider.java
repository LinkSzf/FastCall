package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.BaseDynAuthContent;
import priv.szf.fastcall.common.model.FcAuthProp;
import priv.szf.fastcall.core.FastCallClient;
import priv.szf.fastcall.core.FastCallClientFactory;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.auth.IFcDynAuthProvider;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSourcePak;
import priv.szf.fastcall.core.model.credential.ICredential;

import java.util.Optional;

public abstract class FcBaseInteractiveAuthProvider<C extends BaseDynAuthContent, R>
        extends FcBaseDynAuthProvider<C>
        implements IFcDynAuthProvider<C>
{

    protected abstract ICredential buildCredential(@NonNull FastCallResponse<R> response, @NonNull C authContent);

    @Override
    protected ICredential buildCredential(@NonNull C authContent,
                                                   @NonNull Request request,
                                                   Response response,
                                                   @NonNull String system)
    {
        FcSourcePak sourcePak = getSource().getSourcePak(system);
        FcAuthPak authPak = sourcePak.getAuth();

        FcAuthProp authProp = Optional.ofNullable(authContent.getProp()).orElse(new FcAuthProp());
        FastCallClient client = FastCallClientFactory.getExistedClient(system);
        FastCallResponse<R> authResponse = client.<R>newCall()
                .host(authPak.getParticularHost())
                .uri(authPak.getPath())
                .params(authProp.getParams())
                .method(FcRequestMethod.POST)
                .header(FcHttpHeader.CONTENT_TYPE, FcMediaType.APPLICATION_JSON)
                .header(FcHttpHeader.ACCEPT, FcMediaType.APPLICATION_JSON)
                .headers(authProp.getHeaders())
                .body(authProp.getBody())
                .prepared()
                .anonymousCallIt();

        checkSuccess(authResponse);

        return buildCredential(authResponse, authContent);
    }

    protected void checkSuccess(FastCallResponse<R> response) {
        if (!response.isSuccessful()) {
            throw new FastCallException("系统刷新认证失败，code[%s], message[%s], data[%s]",
                    response.getCode(), response.getMessage(), response.getData()
            );
        }
    }
}

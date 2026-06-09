package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.core.auth.IFcDynAuthProvider;
import priv.szf.fastcall.common.model.content.BaseDynAuthContent;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.Optional;

public abstract class FcBaseDynAuthProvider<C extends BaseDynAuthContent>
        extends FcBaseAuthProvider<C>
        implements IFcDynAuthProvider {

    private static final int DEFAULT_UNAUTHORIZED_CODE = 401;

    protected ICredential buildCredential(@NonNull C authContent,
                                                   @NonNull Request request,
                                                            Response response,
                                                   @NonNull String system) {
        return buildCredential(authContent);
    }

    @Override
    public final Integer getUnauthorizedCode(String system) {
        FcSourcePak sourcePak = getSource().getSourcePak(system);
        return Optional.ofNullable(sourcePak)
                .map(FcSourcePak::getAuth)
                .map(FcAuthPak::getUnauthorizedCode)
                .orElse(DEFAULT_UNAUTHORIZED_CODE);
    }

    @Override
    public final void refreshCredential(Request request, Response response, String system) {
        ICredential credential = buildCredential(getAuthContent(system), request, response, system);
        getSource().updateCredential(system, credential);
    }

}

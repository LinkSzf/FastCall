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
        implements IFcDynAuthProvider<C>
{

    protected abstract ICredential buildCredential(@NonNull C authContent,
                                                   @NonNull Request request,
                                                            Response response,
                                                   @NonNull String system);

    @Override
    protected ICredential buildCredential(@NonNull C authContent) {
        return null;
    }

    @Override
    public Integer getAuthInNeedCode(String system) {
        FcSourcePak sourcePak = getSource().getSourcePak(system);
        return Optional.of(sourcePak)
                .map(FcSourcePak::getAuth)
                .map(FcAuthPak::getStatusCode)
                .orElse(null);
    }

    @Override
    public void refreshCredential(Request request, Response response, String system) {
        ICredential credential = buildCredential(getAuthContent(system), request, response, system);
        getSource().updateCredential(system, credential);
    }

}

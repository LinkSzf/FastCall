package io.github.linkszf.fastcall.core.auth;

import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.common.model.credential.ICredential;

/**
 * Builds the credential of one {@code FcAuthType} from the auth content of the {@code FcRequestContext}.
 * Invoked when the cached credential is missing or invalid; implementations are Spring beans.
 */
public interface IFcCredentialProvider {

    FcAuthType getAuthType();


    ICredential buildCredential(FcRequestContext context);

}

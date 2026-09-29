package priv.szf.fastcall.core.auth;


/**
 * Marker for credential providers whose credentials can be rebuilt on demand.
 * {@code FcAuthSupporter} refreshes an auth type only when its provider implements this interface.
 */
public interface IFcDynCredentialProvider extends IFcCredentialProvider {


}

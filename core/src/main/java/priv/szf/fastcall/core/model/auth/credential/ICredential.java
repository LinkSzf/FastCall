package priv.szf.fastcall.core.model.auth.credential;

public interface ICredential {

    String getAuthString();

    boolean isInvalid();

    default void invalidate() {}


}

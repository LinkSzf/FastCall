package priv.szf.fastcall.core.model.credential;

public interface ICredential {

    String getAuthString();

    boolean isInvalid();

    default void invalidate() {}


}

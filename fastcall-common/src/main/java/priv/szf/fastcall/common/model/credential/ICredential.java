package priv.szf.fastcall.common.model.credential;

public interface ICredential {

    String getAuthString();

    boolean isInvalid();

    default void invalidate() {}


}

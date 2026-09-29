package io.github.linkszf.fastcall.common.model.credential;

import java.io.Serializable;

public interface ICredential extends Serializable {

    String getAuthString();

    boolean isInvalid();

    default void invalidate() {}


}

package io.github.linkszf.fastcall.common.source;


import io.github.linkszf.fastcall.common.model.FcSourcePak;
import io.github.linkszf.fastcall.common.model.credential.ICredential;

public interface IFcSource {

    FcSourcePak getSourcePak(String system);

    void updateCredential(String system, ICredential credential);

    default void init() {}

}

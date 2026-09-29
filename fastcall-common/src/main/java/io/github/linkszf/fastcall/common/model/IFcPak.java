package io.github.linkszf.fastcall.common.model;

import java.io.Serializable;

public interface IFcPak extends Serializable, IFcNonNullModel, IFcImmutableModel {

    default void init() {
        check();
        immunize();
    }



}

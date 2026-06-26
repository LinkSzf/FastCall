package priv.szf.fastcall.common.model;

import java.io.Serializable;

public interface IFcPak extends Serializable, IFcNonNullModel, IFcImmutableModel {


    default <T extends IFcPak> T init() {
        return (T)this.check();
    }



}

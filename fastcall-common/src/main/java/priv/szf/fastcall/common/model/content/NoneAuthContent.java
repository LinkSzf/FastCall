package priv.szf.fastcall.common.model.content;

import priv.szf.fastcall.common.FcAuthType;

import java.io.Serializable;
import java.util.Objects;

public class NoneAuthContent extends BaseAuthContent implements Serializable {

    private static final long serialVersionUID = -577676669729490052L;

    private static volatile NoneAuthContent INSTANCE;

    private NoneAuthContent() {
        this.setType(FcAuthType.NONE);
    }

    public static NoneAuthContent getInstance() {
        NoneAuthContent local = INSTANCE;
        if (Objects.isNull(local)) {
            synchronized (NoneAuthContent.class) {
                local = INSTANCE;
                if (Objects.isNull(local)) {
                    local = new NoneAuthContent();
                    INSTANCE = local;
                }
            }
        }
        return local;
    }

    private Object readResolve() {
        return getInstance();
    }
}

package priv.szf.fastcall.core.call;


import priv.szf.fastcall.core.common.FcBizException;

import java.util.Objects;

public class PriorityValue<T> {

    private T value;

    public PriorityValue() {
        this.value = null;
    }

    public PriorityValue<T> next(T candidate) {
        if (Objects.isNull(value)) {
            this.value = candidate;
        }
        return this;
    }

    public T getValue() {
        if (Objects.isNull(value)) {
            throw new FcBizException("值不能为空");
        }
        return value;
    }

}

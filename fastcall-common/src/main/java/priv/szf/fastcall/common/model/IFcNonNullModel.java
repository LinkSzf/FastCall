package priv.szf.fastcall.common.model;

import cn.hutool.core.collection.CollectionUtil;
import priv.szf.fastcall.common.exception.FcSourceAbsenceException;

import java.util.List;
import java.util.function.Supplier;

@FunctionalInterface
public interface IFcNonNullModel {

    List<Supplier<?>> requireNonNull();

    default <T> T check() {
        T tThis = (T)this;
        List<Supplier<?>> functions = requireNonNull();
        if (CollectionUtil.isEmpty(functions)) {
            return tThis;
        }

        int index = 1;
        for (Supplier<?> f : functions) {
            Object value = f.get();

            if (value == null) {
                throw new FcSourceAbsenceException("Required system info [{}-property{}] is not set", this.getClass().getSimpleName(), index);
            }

            if (value instanceof IFcNonNullModel) {
                ((IFcNonNullModel) value).check();
            }

            index++;
        }
        return tThis;
    }

}

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
                throw new FcSourceAbsenceException("必需系统信息[{}-属性{}]未设置", this.getClass().getSimpleName(), index);
            }

            if (value instanceof IFcNonNullModel) {
                ((IFcNonNullModel) value).check();
            }

            index++;
        }
        return tThis;
    }

}

package priv.szf.fastcall.core.model;

import cn.hutool.core.collection.CollectionUtil;
import priv.szf.fastcall.common.exception.FcSourceAbsenceException;

import java.util.List;
import java.util.function.Function;

public interface IEssentialCheck<T> {

    List<Function<T, ?>> requireNonNull();

    default T check() {
        T tThis = (T)this;
        List<Function<T, ?>> functions = requireNonNull();
        if (CollectionUtil.isEmpty(functions)) {
            return tThis;
        }

        int index = 1;
        for (Function<T, ?> f : functions) {
            Object value = f.apply(tThis);

            if (value == null) {
                throw new FcSourceAbsenceException("必需系统信息[%s-属性%s]未设置", this.getClass().getSimpleName(), index);
            }

            if (value instanceof IEssentialCheck) {
                ((IEssentialCheck<?>) value).check();
            }

            index++;
        }
        return tThis;
    }

}

package priv.szf.fastcall.core.model;

import cn.hutool.core.collection.CollectionUtil;
import priv.szf.fastcall.core.common.exception.FcSourceAbsenceException;

import java.util.List;
import java.util.function.Function;

public interface IEssentialCheck<T> {

    List<Function<T, ?>> checkThese();

    default T check() {
        T tThis = (T)this;
        List<Function<T, ?>> functions = checkThese();
        if (CollectionUtil.isEmpty(functions)) {
            return tThis;
        }

        for (Function<T, ?> f : functions) {
            Object value = f.apply(tThis);
            if (value == null) {
                String methodName = f.toString();
                throw new FcSourceAbsenceException("必需系统信息[%s-%s]未设置", this.getClass().getSimpleName(), methodName);
            }
            if (value instanceof IEssentialCheck) {
                ((IEssentialCheck<?>) value).check();
            }
        }
        return tThis;
    }

}

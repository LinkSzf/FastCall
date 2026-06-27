package priv.szf.fastcall.common.model;


import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IFcImmutableModel {

    default void immunize() {
        Field[] fields = ReflectUtil.getFields(this.getClass());
        for (Field field : fields) {
            Object fieldValue = ReflectUtil.getFieldValue(this, field);
            if (fieldValue instanceof IFcImmutableModel) {
                ((IFcImmutableModel)fieldValue).immunize();
                continue;
            }

            Collection<?> collection;
            Object newValue;
            if (fieldValue instanceof List) {
                collection = (List<?>)fieldValue;
                newValue = Collections.unmodifiableList((List<?>)fieldValue);
            }
            else if (fieldValue instanceof Set) {
                collection = (Set<?>)fieldValue;
                newValue = Collections.unmodifiableSet((Set<?>)fieldValue);
            }
            else if (fieldValue instanceof Collection) {
                collection = (Collection<?>)fieldValue;
                newValue = Collections.unmodifiableCollection((Collection<?>)fieldValue);
            }
            else if (fieldValue instanceof Map) {
                collection = ((Map<?, ?>) fieldValue).values();
                newValue = Collections.unmodifiableMap((Map<?, ?>)fieldValue);
            }
            else {
                continue;
            }

            collection.stream()
                    .filter(o -> o instanceof IFcImmutableModel)
                    .map(o -> (IFcImmutableModel)o)
                    .forEach(IFcImmutableModel::immunize);

            ReflectUtil.setFieldValue(this, field, newValue);
        }
    }


}

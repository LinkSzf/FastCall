package priv.szf.fastcall.common.json;

import java.lang.reflect.Type;
import java.util.Map;


public interface FcJsonCodec {

    String write(Object value);

    <T> T read(String json, Type type);

    default <T> T read(String json, Class<T> type) {
        return read(json, (Type) type);
    }

    Map<String, Object> toPropertyMap(Object bean);

    default Object toScalar(Object value) {
        return read(write(value), Object.class);
    }
}

package priv.szf.fastcall.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FcUtils {

    public static long encodeId(@NonNull String id) {
        return id.hashCode();
    }


}

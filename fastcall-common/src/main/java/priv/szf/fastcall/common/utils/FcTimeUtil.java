package priv.szf.fastcall.common.utils;

import cn.hutool.core.util.ObjUtil;
import lombok.NoArgsConstructor;
import priv.szf.fastcall.common.FcTimeSpan;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class FcTimeUtil {

    public static boolean isInSameTimeSpan(FcTimeSpan timeSpan, LocalDateTime time1, LocalDateTime time2) {
        if (ObjUtil.hasNull(time1, time2)) {
            return false;
        }
        ChronoUnit unit = ChronoUnit.valueOf(timeSpan.name());
        long between = unit.between(time1, time2);
        return Math.abs(between) < 1;
    }

    public static boolean isWithinDuration(FcTimeSpan timeSpan, LocalDateTime time1, LocalDateTime time2, long duration) {
        if (ObjUtil.hasNull(time1, time2)) {
            return false;
        }
        ChronoUnit unit = ChronoUnit.valueOf(timeSpan.name());
        long until = time1.until(time2, unit);
        return Math.abs(until) <= duration;
    }



}

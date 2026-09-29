package io.github.linkszf.fastcall.common.utils;

import cn.hutool.core.util.ObjUtil;
import lombok.NoArgsConstructor;
import io.github.linkszf.fastcall.common.FcTimeSpan;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class FcTimeUtil {

    public static boolean isInSameTimeSpan(FcTimeSpan timeSpan, LocalDateTime time1, LocalDateTime time2) {
        if (ObjUtil.hasNull(time1, time2)) {
            return false;
        }

        // Use a different comparison strategy for the larger units
        switch (timeSpan) {
            case DAYS:
                return time1.toLocalDate().equals(time2.toLocalDate());
            case WEEKS:
                // Determine whether both fall in the same week (ISO standard, weeks start on Monday)
                return time1.toLocalDate().get(WeekFields.ISO.weekOfWeekBasedYear()) ==
                        time2.toLocalDate().get(WeekFields.ISO.weekOfWeekBasedYear()) &&
                        time1.getYear() == time2.getYear();
            case MONTHS:
                return time1.getYear() == time2.getYear() &&
                        time1.getMonth() == time2.getMonth();
            case YEARS:
                return time1.getYear() == time2.getYear();
            default:
                ChronoUnit unit = ChronoUnit.valueOf(timeSpan.name());
                return time1.truncatedTo(unit).equals(time2.truncatedTo(unit));
        }
    }

    public static boolean isWithinDuration(FcTimeSpan timeSpan, LocalDateTime time1, LocalDateTime time2, long duration) {
        if (ObjUtil.hasNull(time1, time2)) {
            return false;
        }
        ChronoUnit unit = ChronoUnit.valueOf(timeSpan.name());
        long until = time1.until(time2, unit);
        return Math.abs(until) <= duration;
    }

    public static LocalDateTime ofMillis(long millis, String zoneId) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.of(zoneId))
                .toLocalDateTime();
    }

    public static LocalDateTime ofMillis(long millis) {
        return ofMillis(millis, ZoneId.systemDefault().getId());
    }


}

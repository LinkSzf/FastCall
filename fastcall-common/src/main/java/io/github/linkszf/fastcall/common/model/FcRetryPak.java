package io.github.linkszf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Retry configuration, scoped to the client (SystemPak) level
 */
@Builder
@Getter
public class FcRetryPak implements IFcPak {

    private static final long serialVersionUID = -6648577437597825361L;

    /** Maximum retry attempts; a value less than or equal to 0 means no retry */
    private final int attempts;

    /** Retry interval in milliseconds; a value less than 0 is treated as 0 */
    private final long duration;

    public boolean isEnabled() {
        return this.attempts > 0;
    }

    @Override
    public List<Supplier<?>> requireNonNull() {
        return Collections.emptyList();
    }
}

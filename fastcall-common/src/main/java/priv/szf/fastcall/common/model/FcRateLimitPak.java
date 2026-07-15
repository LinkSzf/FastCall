package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import priv.szf.fastcall.common.FcTimeSpan;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Builder
@Getter
public class FcRateLimitPak implements IFcPak {

    private static final long serialVersionUID = 5704305981571184483L;

    private final Long id;

    private final Long systemId;

    private final FcTimeSpan span;

    private final Long maximum;

    @Setter
    private volatile LocalDateTime lastTime;

    @Setter
    private volatile long currentCount;


    @Override
    public List<Supplier<?>> requireNonNull() {
        return Arrays.asList(
                this::getSystemId,
                this::getSpan,
                this::getMaximum
        );
    }
}

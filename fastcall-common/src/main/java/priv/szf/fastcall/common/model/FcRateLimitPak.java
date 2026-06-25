package priv.szf.fastcall.common.model;

import lombok.Data;
import priv.szf.fastcall.common.FcTimeSpan;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Data
public class FcRateLimitPak implements IEssentialCheck<FcRateLimitPak>, Serializable {

    private static final long serialVersionUID = 5704305981571184483L;

    private Long id;

    private Long systemId;

    private FcTimeSpan span;

    private LocalDateTime lastTime;

    private Long maximum;

    private long current;


    @Override
    public List<Function<FcRateLimitPak, ?>> requireNonNull() {
        return Arrays.asList(
                FcRateLimitPak::getSystemId,
                FcRateLimitPak::getSpan,
                FcRateLimitPak::getMaximum
        );
    }
}

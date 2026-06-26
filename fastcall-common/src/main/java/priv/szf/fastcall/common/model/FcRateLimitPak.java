package priv.szf.fastcall.common.model;

import lombok.Data;
import priv.szf.fastcall.common.FcTimeSpan;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Data
public class FcRateLimitPak implements IFcPak {

    private static final long serialVersionUID = 5704305981571184483L;

    private Long id;

    private Long systemId;

    private FcTimeSpan span;

    private LocalDateTime lastTime;

    private Long maximum;

    private long current;


    @Override
    public List<Supplier<?>> requireNonNull() {
        return Arrays.asList(
                this::getSystemId,
                this::getSpan,
                this::getMaximum
        );
    }
}

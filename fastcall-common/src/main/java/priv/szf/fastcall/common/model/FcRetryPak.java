package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * 重试配置，属于client(SystemPak)级别的配置
 */
@Builder
@Getter
public class FcRetryPak implements IFcPak {

    private static final long serialVersionUID = -6648577437597825361L;

    /** 最大重试次数，小于等于0表示不重试 */
    private final int attempts;

    /** 重试间隔（毫秒），小于0时按0处理 */
    private final long duration;

    public boolean isEnabled() {
        return this.attempts > 0;
    }

    @Override
    public List<Supplier<?>> requireNonNull() {
        return Collections.emptyList();
    }
}

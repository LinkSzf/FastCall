package priv.szf.fastcall.common.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@Getter
public class FastCallException extends RuntimeException {

    private static final long serialVersionUID = -1623901843036903461L;

    public FastCallException(String message, Object... args) {
        super(buildMessage(message, args));
    }

    public FastCallException(Throwable e, String message, Object... args) {
        super(buildMessage(message, args), e);
    }

    private static String buildMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        return StrUtil.format(message, args);
    }


}

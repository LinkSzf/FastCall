package priv.szf.fastcall.common.exception;

import lombok.Getter;

@Getter
public class FastCallException extends RuntimeException {

    private static final long serialVersionUID = -1623901843036903461L;

    private static final String MODEL_PREFIX = "FastCall-";

    public FastCallException(String message, Object... args) {
        super(buildMessage(message, args));
    }

    public FastCallException(Throwable e, String message, Object... args) {
        super(buildMessage(message, args), e);
    }

    private static String buildMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return MODEL_PREFIX + message;
        }
        return MODEL_PREFIX + String.format(message, args);
    }


}

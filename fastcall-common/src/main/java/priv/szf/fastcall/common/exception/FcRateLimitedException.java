package priv.szf.fastcall.common.exception;

public class FcRateLimitedException extends FastCallException {
    public FcRateLimitedException(String message, Object... args) {
        super(message,  args);
    }
}

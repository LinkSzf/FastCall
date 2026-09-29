package io.github.linkszf.fastcall.common.exception;

public class FcUnexpectedException extends FastCallException {

    public FcUnexpectedException(String message, Object... args) {
        super(message, args);
    }

    public FcUnexpectedException(Throwable e, String message, Object... args) {
        super(e, message, args);
    }
}

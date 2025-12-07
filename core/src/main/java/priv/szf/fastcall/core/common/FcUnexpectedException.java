package priv.szf.fastcall.core.common;

public class FcUnexpectedException extends FcBizException {

    private final static String CODE = "unexpected error";

    public FcUnexpectedException(String message) {
        super(CODE, message);
    }

    public FcUnexpectedException(Throwable e, String message) {
        super(e, CODE, message);
    }
}

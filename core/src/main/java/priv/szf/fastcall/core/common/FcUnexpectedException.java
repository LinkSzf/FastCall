package priv.szf.fastcall.core.common;

public class FcUnexpectedException extends FcBizException {

    private final static String code = "unexpected error";

    public FcUnexpectedException(String message) {
        super(code, message);
    }
}

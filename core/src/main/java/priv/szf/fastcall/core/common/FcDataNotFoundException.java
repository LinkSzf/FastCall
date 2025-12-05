package priv.szf.fastcall.core.common;

public class FcDataNotFoundException extends FcBizException {
    public static final String DATA_NOT_FOUND_CODE = "data not found";

    public FcDataNotFoundException() {
        super(DATA_NOT_FOUND_CODE);
    }

    public FcDataNotFoundException(String message) {
        super(DATA_NOT_FOUND_CODE, message);
    }
}

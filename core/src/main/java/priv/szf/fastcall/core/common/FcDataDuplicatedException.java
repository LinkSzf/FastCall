package priv.szf.fastcall.core.common;

public class FcDataDuplicatedException extends FcBizException {
    public static final String DATA_NOT_FOUND_CODE = "data duplicated";

    public FcDataDuplicatedException(String message) {
        super(DATA_NOT_FOUND_CODE, message);
    }


}

package priv.szf.fastcall.core.common;

import lombok.Getter;

@Getter
public class FcBizException extends RuntimeException {

    private final String code;

    private final String message;

    public FcBizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public FcBizException(String message) {
        super(message);
        this.code = message;
        this.message = message;
    }

    public FcBizException(Throwable e, String code, String message) {
        super(message, e);
        this.code = code;
        this.message = message;
    }
}

package priv.szf.fastcall.core.call;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FastCallResponse<T> {

    private final int code;

    private final String message;

    private final boolean isSuccessful;

    private final T data;

}

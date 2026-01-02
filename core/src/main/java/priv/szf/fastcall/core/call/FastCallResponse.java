package priv.szf.fastcall.core.call;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class FastCallResponse<T> {

    private final int code;

    private final String message;

    private final boolean isSuccessful;

    @JsonIgnore
    private final Map<String, List<String>> headers;

    private final T data;

}

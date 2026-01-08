package priv.szf.fastcall.test.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import priv.szf.fastcall.core.FastCallResponse;

import java.util.List;
import java.util.Map;

@Data
public class TestResponse {

    private final int code;

    private final String message;

    private final boolean isSuccessful;

    @JsonIgnore
    private final Map<String, List<String>> headers;

    private final Object data;


    public TestResponse(FastCallResponse<Object> response) {
        this.code = response.getCode();
        this.message = response.getMessage();
        this.isSuccessful = response.isSuccessful();
        this.headers = response.getHeaders();
        this.data = response.getData();
    }
}

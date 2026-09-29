package io.github.linkszf.fastcall.test.model;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import io.github.linkszf.fastcall.core.FastCallResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class TestResponse {

    private final int code;

    private final String message;

    private final boolean isSuccessful;

    @JsonIgnore
    private final Map<String, List<String>> headers;

    private final Object data;

    private final String exception;


    public TestResponse(FastCallResponse<Object> response) {
        this.code = response.getCode();
        this.message = response.getMessage();
        this.isSuccessful = response.isSuccessful();
        this.headers = response.getHeaders();
        this.data = response.getData();
        this.exception = Optional.ofNullable(response.getException()).map(ExceptionUtil::stacktraceToString).orElse(null);
    }
}

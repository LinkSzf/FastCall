package io.github.linkszf.fastcall.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BearerAuthResponseBody {

    private long timestamp;

    private System system;


    @AllArgsConstructor
    @Data
    public static class System {
        private String token;

        private long expire;

    }


}

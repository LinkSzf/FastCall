package priv.szf.fastcall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FcRequestMethod {
    
    GET("GET"),
    
    POST("POST"),
    
    PUT("PUT"),
    
    DELETE("DELETE"),
    
    PATCH("PATCH");
    
    private final String name;
}

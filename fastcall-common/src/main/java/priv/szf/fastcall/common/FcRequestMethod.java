package priv.szf.fastcall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import priv.szf.fastcall.common.exception.FastCallException;

@Getter
@AllArgsConstructor
public enum FcRequestMethod {
    
    GET("GET"),
    
    POST("POST"),
    
    PUT("PUT"),
    
    DELETE("DELETE"),
    
    PATCH("PATCH"),

    HEAD("HEAD"),

    OPTIONS("OPTIONS"),

    TRACE("TRACE"),

    CONNECT("CONNECT");
    
    private final String name;

    public static FcRequestMethod parse(String name) {
        if (name == null) {
            return null;
        }
        for (FcRequestMethod value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        throw new FastCallException("Unsupported request method[{}]", name);
    }
}

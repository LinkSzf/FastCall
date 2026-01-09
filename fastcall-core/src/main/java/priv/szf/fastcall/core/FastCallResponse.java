package priv.szf.fastcall.core;


import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Builder
@Data
public class FastCallResponse<T> {

    private final int code;

    private final String message;

    private final boolean isSuccessful;

    private final Map<String, List<String>> headers;

    private final T data;

    public String getSingleHeader(String name) {
        return Optional.ofNullable(headers)
                .map(map -> map.get(name))
                .map(list -> list.get(0))
                .orElse(null);
    }

    public List<String> getHeaders(String name) {
        return Optional.ofNullable(headers)
                .map(map -> map.get(name))
                .orElse(Collections.emptyList());
    }

}

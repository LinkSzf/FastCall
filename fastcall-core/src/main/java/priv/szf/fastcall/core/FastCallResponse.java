package priv.szf.fastcall.core;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Builder
@Getter
public class FastCallResponse<T> {

    private final int code;

    private final String message;

    private final T data;

    private final Map<String, List<String>> headers;

    private final String mediaType;

    private final boolean isSuccessful;

    private final boolean isConnected;

    private final boolean isCached;

    private final boolean isRedirect;

    private final Exception exception;

    private final LocalDateTime requestTime;

    private final LocalDateTime responseTime;

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

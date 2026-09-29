package io.github.linkszf.fastcall.core.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import io.github.linkszf.fastcall.common.event.request.IFcRequestEvent;
import io.github.linkszf.fastcall.core.FastCallResponse;
import io.github.linkszf.fastcall.core.event.FcApiRequestEvent;
import io.github.linkszf.fastcall.core.event.FcAuthRequestEvent;
import io.github.linkszf.fastcall.core.event.FcRequestEvent;
import io.github.linkszf.fastcall.core.event.IFcRequestEventPublisher;

import java.util.Objects;
import java.util.Optional;

/**
 * Publishes a request event for every call in a finally block, so failed calls are reported too.
 * Ordered outside {@code FcRetryFilter}, it emits one api, auth or generic event per call.
 */
@RequiredArgsConstructor
@Order(1000)
public class FcRequestEventFilter implements FcFilter {

    private final IFcRequestEventPublisher eventPublisher;

    @Override
    public void doFilter(FcFilterContext context, FcFilterChain chain) {
        try {
            chain.doFilter(context);
        } finally {
            this.eventPublisher.publish(createEvent(context));
        }
    }

    private IFcRequestEvent createEvent(FcFilterContext context) {
        boolean successful = Optional.of(context)
                .map(FcFilterContext::getResponse)
                .map(FastCallResponse::isSuccessful)
                .orElse(false);
        if (Objects.nonNull(context.getApiName())) {
            return FcApiRequestEvent.builder()
                    .system(context.getSystem())
                    .api(context.getApiName())
                    .url(context.getUrl())
                    .success(successful)
                    .build();
        }
        if (context.isAuth()) {
            return FcAuthRequestEvent.builder()
                    .system(context.getSystem())
                    .url(context.getUrl())
                    .success(successful)
                    .build();
        }
        return new FcRequestEvent(context.getUrl(), successful);
    }
}

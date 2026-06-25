package priv.szf.fastcall.core.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import priv.szf.fastcall.common.event.request.IFcRequestEvent;
import priv.szf.fastcall.common.filter.FcFilter;
import priv.szf.fastcall.common.filter.FcFilterChain;
import priv.szf.fastcall.common.filter.FcFilterContext;
import priv.szf.fastcall.core.event.FcApiRequestEvent;
import priv.szf.fastcall.core.event.FcAuthRequestEvent;
import priv.szf.fastcall.core.event.FcRequestEvent;
import priv.szf.fastcall.core.event.IFcRequestEventPublisher;

import java.util.Objects;

@RequiredArgsConstructor
@Order(100)
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
        if (Objects.nonNull(context.getApiName())) {
            return FcApiRequestEvent.builder()
                    .system(context.getSystem())
                    .api(context.getApiName())
                    .url(context.getUrl())
                    .success(context.isSuccess())
                    .build();
        }
        if (context.isAuth()) {
            return FcAuthRequestEvent.builder()
                    .system(context.getSystem())
                    .url(context.getUrl())
                    .success(context.isSuccess())
                    .build();
        }
        return new FcRequestEvent(context.getUrl(), context.isSuccess());
    }
}

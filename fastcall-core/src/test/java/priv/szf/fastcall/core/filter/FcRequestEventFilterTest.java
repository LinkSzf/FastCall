package priv.szf.fastcall.core.filter;

import org.junit.jupiter.api.Test;
import priv.szf.fastcall.common.event.IFcEvent;
import priv.szf.fastcall.common.filter.FcFilterContext;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.core.event.FcApiRequestEvent;
import priv.szf.fastcall.core.event.FcAuthRequestEvent;
import priv.szf.fastcall.core.event.FcRequestEvent;
import priv.szf.fastcall.core.event.IFcRequestEventPublisher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FcRequestEventFilterTest {

    @Test
    void should_publish_api_request_event_when_chain_succeeds() {
        RecordingPublisher publisher = new RecordingPublisher();
        FcRequestEventFilter filter = new FcRequestEventFilter(publisher);

        FcFilterContext context = FcFilterContext.builder()
                .system("demo")
                .apiName("queryUser")
                .url("http://localhost/query")
                .sourcePak(buildSourcePak())
                .build();

        filter.doFilter(context, ctx -> ctx.setSuccess(true));

        assertEquals(1, publisher.events.size());
        IFcEvent event = publisher.events.get(0);
        assertInstanceOf(FcApiRequestEvent.class, event);
        FcApiRequestEvent apiRequestEvent = (FcApiRequestEvent) event;
        assertEquals("demo", apiRequestEvent.getSystem());
        assertEquals("queryUser", apiRequestEvent.getApi());
        assertTrue(apiRequestEvent.isSuccess());
    }

    @Test
    void should_publish_failed_auth_request_event_when_chain_throws() {
        RecordingPublisher publisher = new RecordingPublisher();
        FcRequestEventFilter filter = new FcRequestEventFilter(publisher);

        FcFilterContext context = FcFilterContext.builder()
                .system("demo")
                .auth(true)
                .url("http://localhost/auth")
                .sourcePak(buildSourcePak())
                .build();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> filter.doFilter(context, ctx -> {
                    throw new RuntimeException("boom");
                }));

        assertEquals("boom", ex.getMessage());
        assertEquals(1, publisher.events.size());
        IFcEvent event = publisher.events.get(0);
        assertInstanceOf(FcAuthRequestEvent.class, event);
        FcAuthRequestEvent authRequestEvent = (FcAuthRequestEvent) event;
        assertEquals("demo", authRequestEvent.getSystem());
        assertFalse(authRequestEvent.isSuccess());
    }

    @Test
    void should_publish_plain_request_event_for_non_api_non_auth_call() {
        RecordingPublisher publisher = new RecordingPublisher();
        FcRequestEventFilter filter = new FcRequestEventFilter(publisher);

        FcFilterContext context = FcFilterContext.builder()
                .system("demo")
                .url("http://localhost/plain")
                .sourcePak(buildSourcePak())
                .build();

        filter.doFilter(context, ctx -> ctx.setSuccess(false));

        assertEquals(1, publisher.events.size());
        assertInstanceOf(FcRequestEvent.class, publisher.events.get(0));
    }

    private FcSourcePak buildSourcePak() {
        FcSystemPak systemPak = new FcSystemPak();
        systemPak.setCode("demo");
        return FcSourcePak.builder()
                .system(systemPak)
                .build();
    }

    private static final class RecordingPublisher implements IFcRequestEventPublisher {

        private final List<IFcEvent> events = new ArrayList<>();

        @Override
        public void publish(IFcEvent event) {
            events.add(event);
        }

        @Override
        public void publishAll(Collection<IFcEvent> events) {
            this.events.addAll(events);
        }
    }
}

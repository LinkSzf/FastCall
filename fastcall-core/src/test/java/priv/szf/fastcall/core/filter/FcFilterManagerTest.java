package priv.szf.fastcall.core.filter;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.Order;
import priv.szf.fastcall.common.filter.FcFilter;
import priv.szf.fastcall.common.filter.FcFilterChain;
import priv.szf.fastcall.common.filter.FcFilterContext;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.FcSystemPak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FcFilterManagerTest {

    @Test
    void should_execute_filters_as_responsibility_chain() {
        List<String> traces = new ArrayList<>();
        FcFilter first = new FirstTraceFilter(traces);
        FcFilter second = new SecondTraceFilter(traces);
        FcFilterManager manager = new FcFilterManager(Arrays.asList(second, first));

        FcFilterContext context = FcFilterContext.builder()
                .system("demo")
                .sourcePak(buildSourcePak())
                .url("http://localhost/demo")
                .build();

        manager.doFilter(context, ctx -> traces.add("terminal"));

        assertEquals(
                Arrays.asList(
                        "first-before",
                        "second-before",
                        "terminal",
                        "second-after",
                        "first-after"
                ),
                traces
        );
    }

    private FcSourcePak buildSourcePak() {
        FcSystemPak systemPak = new FcSystemPak();
        systemPak.setCode("demo");
        return FcSourcePak.builder()
                .system(systemPak)
                .build();
    }

    @Order(100)
    private static final class FirstTraceFilter implements FcFilter {

        private final String name;

        private final List<String> traces;

        private FirstTraceFilter(List<String> traces) {
            this.name = "first";
            this.traces = traces;
        }

        @Override
        public void doFilter(FcFilterContext context, FcFilterChain chain) {
            traces.add(name + "-before");
            chain.doFilter(context);
            traces.add(name + "-after");
        }
    }

    @Order(200)
    private static final class SecondTraceFilter implements FcFilter {

        private final String name;

        private final List<String> traces;

        private SecondTraceFilter(List<String> traces) {
            this.name = "second";
            this.traces = traces;
        }

        @Override
        public void doFilter(FcFilterContext context, FcFilterChain chain) {
            traces.add(name + "-before");
            chain.doFilter(context);
            traces.add(name + "-after");
        }
    }
}

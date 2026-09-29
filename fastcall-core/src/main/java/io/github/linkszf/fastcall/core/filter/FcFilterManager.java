package io.github.linkszf.fastcall.core.filter;


import cn.hutool.core.collection.CollectionUtil;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Collections;
import java.util.List;

/**
 * Holds the ordered {@code FcFilter} beans, sorted by {@code @Order} and initialized once.
 * Each call runs a fresh chain that finally delegates to the terminal HTTP call.
 */
public class FcFilterManager {

    private final List<FcFilter> filters;


    public FcFilterManager(List<FcFilter> filters) {
        if (CollectionUtil.isEmpty(filters)) {
            this.filters = Collections.emptyList();
            return;
        }

        AnnotationAwareOrderComparator.sort(filters);
        filters.forEach(FcFilter::init);
        this.filters = Collections.unmodifiableList(filters);
    }

    public void doFilter(FcFilterContext context, FcFilterChain terminalChain) {
        DefaultFcFilterChain.create(filters, terminalChain).doFilter(context);
    }

    private static final class DefaultFcFilterChain implements FcFilterChain {

        private final List<FcFilter> filters;

        private final FcFilterChain terminalChain;

        private int index;

        private DefaultFcFilterChain(List<FcFilter> filters, FcFilterChain terminalChain) {
            this.filters = filters;
            this.terminalChain = terminalChain;
        }

        private static DefaultFcFilterChain create(List<FcFilter> filters, FcFilterChain terminalChain) {
            return new DefaultFcFilterChain(filters, terminalChain);
        }

        @Override
        public void doFilter(FcFilterContext context) {
            if (index < filters.size()) {
                FcFilter filter = filters.get(index++);
                filter.doFilter(context, this);
                return;
            }
            if (terminalChain != null) {
                terminalChain.doFilter(context);
            }
        }
    }

}

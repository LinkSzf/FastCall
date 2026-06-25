package priv.szf.fastcall.core.filter;


import cn.hutool.core.collection.CollectionUtil;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import priv.szf.fastcall.common.filter.FcFilter;
import priv.szf.fastcall.common.filter.FcFilterChain;
import priv.szf.fastcall.common.filter.FcFilterContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FcFilterManager {


    private final List<FcFilter> filters;


    public FcFilterManager(List<FcFilter> filters) {
        if (CollectionUtil.isEmpty(filters)) {
            this.filters = Collections.emptyList();
            return;
        }
        this.filters = new ArrayList<>(filters);
        AnnotationAwareOrderComparator.sort(this.filters);
    }

    public void doFilter(FcFilterContext context, FcFilterChain terminalChain) {
        new DefaultFcFilterChain(filters, terminalChain).doFilter(context);
    }

    private static final class DefaultFcFilterChain implements FcFilterChain {

        private final List<FcFilter> filters;

        private final FcFilterChain terminalChain;

        private int index;

        private DefaultFcFilterChain(List<FcFilter> filters, FcFilterChain terminalChain) {
            this.filters = filters;
            this.terminalChain = terminalChain;
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

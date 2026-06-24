package priv.szf.fastcall.core.filter;


import cn.hutool.core.collection.CollectionUtil;
import priv.szf.fastcall.common.filter.FcFilter;
import priv.szf.fastcall.common.model.FcSourcePak;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FcFilterManager {


    private final List<FcFilter> filters;


    public FcFilterManager(List<FcFilter> filters) {
        if (CollectionUtil.isEmpty(filters)) {
            this.filters = Collections.emptyList();
            return;
        }
        this.filters = filters.stream()
                .sorted(Comparator.comparingInt(FcFilter::getOrder))
                .collect(Collectors.toList());
    }

    public void doFilter(FcSourcePak sourcePak) {
        for (FcFilter filter : filters) {
            filter.doFilter(sourcePak);
        }
    }


}

package priv.szf.fastcall.common.filter;

import priv.szf.fastcall.common.model.FcSourcePak;

public interface FcFilter {

    int getOrder();

    void doFilter(FcSourcePak sourcePak);

    default void OnError(String system, Throwable e) {
    }






}

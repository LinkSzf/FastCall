package priv.szf.fastcall.common.filter;

@FunctionalInterface
public interface FcFilterChain {

    void doFilter(FcFilterContext context);

}

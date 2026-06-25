package priv.szf.fastcall.core.filter;

@FunctionalInterface
public interface FcFilterChain {

    void doFilter(FcFilterContext context);

}

package priv.szf.fastcall.core.filter;

public interface FcFilter {

    void doFilter(FcFilterContext context, FcFilterChain chain);

    default void init() {
    }

}

package priv.szf.fastcall.core.filter;

/**
 * Extension point for a filter that wraps a FastCall request with cross-cutting logic.
 * Beans are ordered by {@code @Order} and initialized once by {@code FcFilterManager}.
 */
public interface FcFilter {

    void doFilter(FcFilterContext context, FcFilterChain chain);

    default void init() {
    }

}

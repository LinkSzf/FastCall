package io.github.linkszf.fastcall.core.filter;

/**
 * Continuation handing the context to the next {@code FcFilter}, or to the HTTP call once the chain ends.
 * Stateful per call: skipping it stops the chain, calling it again resumes with the following filters.
 */
@FunctionalInterface
public interface FcFilterChain {

    void doFilter(FcFilterContext context);

}

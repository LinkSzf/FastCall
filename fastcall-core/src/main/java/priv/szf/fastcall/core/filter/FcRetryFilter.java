package priv.szf.fastcall.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.FcRetryPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.core.FastCallResponse;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Retry filter retrying a failed request per the client-level {@code FcRetryPak} config, ordered last in the chain.
 * Skipped without retry config or when attempts are <= 0; each retry waits {@code duration} milliseconds.
 */
@Slf4j
@Order(Integer.MAX_VALUE)
public class FcRetryFilter implements FcFilter {

    @Override
    public void doFilter(FcFilterContext context, FcFilterChain chain) {
        FcRetryPak retry = resolveRetry(context);
        if (Objects.isNull(retry) || !retry.isEnabled()) {
            chain.doFilter(context);
            return;
        }

        boolean needRetry;
        try {
            chain.doFilter(context);
            needRetry = !isConnected(context);
        } catch (Exception e) {
            needRetry = true;
        }

        if (needRetry) {
            retry(context, chain, retry);
        }
    }

    private void retry(FcFilterContext context, FcFilterChain chain, FcRetryPak retry) {
        int maxAttempts = retry.getAttempts();
        long duration = Math.max(0L, retry.getDuration());

        int attempts = 0;
        while (attempts < maxAttempts) {
            attempts++;
            log.warn("{}-Retrying call, attempt: {}, system: {}, url: {}", FastCallConsts.NAME, attempts, context.getSystem(), context.getUrl());

            sleep(duration);

            try {
                chain.doFilter(context);
                FastCallResponse<?> response = context.getResponse();
                if (response.isConnected()) {
                    return;
                }
            } catch (Exception e) {
                if (attempts >= maxAttempts) {
                    throw e;
                }
            }
        }
    }

    private void sleep(long duration) {
        if (duration <= 0) {
            return;
        }

        try {
            TimeUnit.MILLISECONDS.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FcUnexpectedException(e, "Interrupted when retrying calls.");
        }
    }

    private boolean isConnected(FcFilterContext context) {
        return Optional.of(context)
                .map(FcFilterContext::getResponse)
                .map(FastCallResponse::isConnected)
                .orElse(false);
    }

    private FcRetryPak resolveRetry(FcFilterContext context) {
        return Optional.of(context)
                .map(FcFilterContext::getSourcePak)
                .map(FcSourcePak::getRetry)
                .orElse(null);
    }

}

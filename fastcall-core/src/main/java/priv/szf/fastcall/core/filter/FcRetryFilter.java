package priv.szf.fastcall.core.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.config.FastCallProperties;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Order(Integer.MAX_VALUE)
@RequiredArgsConstructor
public class FcRetryFilter implements FcFilter {

    private final FastCallProperties properties;

    private int maxAttempts;

    private long retryInterval;

    @Override
    public void init() {
        this.maxAttempts = this.properties.getFilter().getRetry().getAttempts();
        this.retryInterval = this.properties.getFilter().getRetry().getInterval();
        check();
    }

    @Override
    public void doFilter(FcFilterContext context, FcFilterChain chain) {
        boolean needRetry;
        try {
            chain.doFilter(context);
            needRetry = !Optional.of(context)
                    .map(FcFilterContext::getResponse)
                    .map(FastCallResponse::isConnected)
                    .orElse(false);
        } catch (Exception e) {
            needRetry = true;
        }

        if (needRetry) {
            retry(context, chain);
        }
    }

    private void retry(FcFilterContext context, FcFilterChain chain) {
        int attempts = 0;
        while (attempts < this.maxAttempts) {
            attempts++;
            try {
                TimeUnit.MILLISECONDS.sleep(retryInterval);
            } catch (InterruptedException e) {
                throw new FcUnexpectedException(e,"Interrupted when retrying calls.");
            }

            try {
                chain.doFilter(context);
                FastCallResponse<?> response = context.getResponse();
                if (response.isConnected()) {
                    return;
                }
            } catch (Exception e) {
                if (attempts >= this.maxAttempts) {
                    throw e;
                }
            }
        }
    }

    private void check() {
        if (this.maxAttempts <= 0) {
            throw new FastCallException("maxAttempts must be greater than 0");
        }
        if (this.retryInterval <= 0) {
            throw new FastCallException("retryInterval must be greater than 0");
        }
    }

}

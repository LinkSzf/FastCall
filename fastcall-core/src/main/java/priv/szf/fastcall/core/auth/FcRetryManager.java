package priv.szf.fastcall.core.auth;

import org.springframework.stereotype.Component;

@Component
public class FcRetryManager {

    private static final ThreadLocal<Boolean> RETRY_FLAG = ThreadLocal.withInitial(() -> false);

    public void setFlag() {
        RETRY_FLAG.set(true);
    }


    public boolean isFlagAvailable() {
        return RETRY_FLAG.get();
    }

    public void removeFlag() {
        RETRY_FLAG.remove();
    }
}

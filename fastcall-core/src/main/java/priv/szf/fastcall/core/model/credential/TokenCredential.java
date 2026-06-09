package priv.szf.fastcall.core.model.credential;

import lombok.AllArgsConstructor;
import lombok.Builder;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@Builder
public class TokenCredential extends BaseCredential implements ICredential {

    private final String token;

    private final LocalDateTime issuance;

    private final LocalDateTime estimatedExpiration;

    @Override
    public String getAuthString() {
        return token;
    }

    @Override
    public boolean isValid() {
        return Objects.nonNull(estimatedExpiration)
                && estimatedExpiration.isBefore(LocalDateTime.now());
    }
}

package priv.szf.fastcall.core.model.credential;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@Builder
@Data
public class TokenCredential implements ICredential {

    private final String token;

    private final LocalDateTime issuance;

    private final LocalDateTime estimatedExpiration;

    @Override
    public String getAuthString() {
        return token;
    }

    @Override
    public boolean isInvalid() {
        return Objects.isNull(estimatedExpiration)
                || estimatedExpiration.isBefore(LocalDateTime.now());
    }
}

package priv.szf.fastcall.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
public class FcTokenPak<T> {

    private T token;

    private LocalDateTime issuance;

    private LocalDateTime estimatedExpiration;

}

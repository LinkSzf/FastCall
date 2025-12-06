package priv.szf.fastcall.core.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FcTokenPak {

    private String token;

    private LocalDateTime issuanceTime;

    private LocalDateTime estimatedExpirationTime;

}

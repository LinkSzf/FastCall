package io.github.linkszf.fastcall.common.model.content;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.linkszf.fastcall.common.FcAuthPosition;

import java.io.Serializable;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class JwtAuthContent extends BaseDynAuthContent implements Serializable {

    private static final long serialVersionUID = 2627469290171843081L;

    private FcAuthPosition positionOn;

    private String algorithm;

    private String secret;

    private Long issueAtOffset;

    private Long expiresIn;

    private Long notValidBefore;

    private Map<String, Object> payload;

    private Map<String, Object> header;



}

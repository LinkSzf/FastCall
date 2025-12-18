package priv.szf.fastcall.core.call.auth.provider.digest;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.common.AuthType;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
public class DigestChallenge {

    private final String realm;

    private final String nonce;

    private final String opaque;

    private final String qop;

    private final String algorithm;

    private final String stale;

    private final String domain;

    private final Map<String, String> otherParams;

    public static DigestChallenge parse(String authenticateHeader) {
        String headerValue = StringUtils.removeStart(authenticateHeader, AuthType.DIGEST.getPrefix());
        String[] params = headerValue.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        Map<String, String> paramMap = new HashMap<>();
        for (String param : params) {
            String[] parts = param.trim().split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            String key = parts[0].trim();
            String value = parts[1].trim().replaceAll("^\"|\"$", "");
            paramMap.put(key.toLowerCase(), value);
        }

        return DigestChallenge.builder()
                .realm(paramMap.remove("realm"))
                .nonce(paramMap.remove("nonce"))
                .opaque(paramMap.remove("opaque"))
                .qop(paramMap.remove("qop"))
                .algorithm(paramMap.remove("algorithm"))
                .stale(paramMap.remove("stale"))
                .domain(paramMap.remove("domain"))
                .otherParams(paramMap)
                .build();
    }


}

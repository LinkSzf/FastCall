package priv.szf.fastcall.core.auth.provider;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.model.credential.TokenCredential;
import priv.szf.fastcall.core.auth.IFcCredentialProvider;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.content.TokenAuthContent;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
public class FcTokenCredentialProvider extends FcBaseInteractiveCredentialProvider<TokenAuthContent, String>
        implements IFcCredentialProvider {

    private static final long DEFAULT_EXPIRES_IN = 60 * 60 * 24 * 7;


    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.BEARER;
    }

    @Override
    protected TokenCredential buildCredential(@NonNull FastCallResponse<String> response,
                                              @NonNull TokenAuthContent authContent
    ) {
        String data = response.getData();
        if (Objects.isNull(data)) {
            throw new FastCallException("Failed to get token, the response body is empty");
        }

        JSONObject jsonData = JSONUtil.parseObj(data);

        String tokenStr = getToken(jsonData, authContent.getTokenField());
        LocalDateTime issuance = getIssuance(jsonData, authContent.getIssuanceField());
        LocalDateTime estimatedExpiration = getEstimatedExpiration(jsonData, authContent.getExpiredInField(), issuance);
        return TokenCredential.builder()
                .token(tokenStr)
                .issuance(issuance)
                .estimatedExpiration(estimatedExpiration)
                .build();
    }

    private LocalDateTime getEstimatedExpiration(JSONObject jsonData, String fieldPath, LocalDateTime issuance) {
        Long expiresIn = jsonData.getByPath(fieldPath, Long.class);
        long expiresInNum = (Objects.isNull(expiresIn)) ? DEFAULT_EXPIRES_IN : expiresIn;
        return issuance.plusSeconds(expiresInNum);
    }

    private LocalDateTime getIssuance(JSONObject jsonData, String fieldPath) {
        String issuance = jsonData.getByPath(fieldPath, String.class);
        if (StrUtil.isNotBlank(issuance)) {
            if (NumberUtil.isLong(issuance)) {
                long issuanceLong = NumberUtil.parseLong(issuance);
                return DateUtil.date(issuanceLong).toLocalDateTime();
            }
            return LocalDateTimeUtil.parse(issuance);
        }
        return LocalDateTime.now();
    }

    private String getToken(JSONObject jsonData, String fieldPath) {
        String token = jsonData.getByPath(fieldPath, String.class);
        if (StrUtil.isBlank(token)) {
            throw new FastCallException("Token field is not found at path[{}] in the response body.", fieldPath);
        }
        return token;
    }

}

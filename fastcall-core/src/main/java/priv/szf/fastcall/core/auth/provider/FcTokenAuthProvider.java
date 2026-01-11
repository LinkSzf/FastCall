package priv.szf.fastcall.core.auth.provider;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.model.credential.TokenCredential;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.content.TokenAuthContent;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class FcTokenAuthProvider extends FcBaseInteractiveAuthProvider<TokenAuthContent, String>
        implements IFcAuthProvider<TokenAuthContent> {

    private static final long DEFAULT_EXPIRED_IN = 3600 * 24 * 7;

    private final IFcSource source;

    @Override
    protected IFcSource getSource() {
        return source;
    }

    @Override
    protected TokenCredential buildCredential(@NonNull FastCallResponse<String> response,
                                              @NonNull TokenAuthContent authContent
    ) {
        String data = response.getData();
        if (Objects.isNull(data)) {
            throw new FastCallException(
                            "系统刷新认证失败，响应体为空，code[%s], message[%s]",
                            response.getCode(), response.getMessage()
            );
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
        Long expiredIn = jsonData.getByPath(fieldPath, Long.class);
        long expiredInNum = (Objects.isNull(expiredIn)) ? DEFAULT_EXPIRED_IN : expiredIn;
        return issuance.plusSeconds(expiredInNum);
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
            throw new FastCallException("响应体[%s]中路径[%s]未找到token字段", jsonData, fieldPath);
        }
        return token;
    }

}

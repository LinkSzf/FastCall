package priv.szf.fastcall.core.call.auth;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.common.TokenConst;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class DefaultAuthProvider<T extends BaseDynAuthContent> implements IAuthProvider<T> {

    private static final long DEFAULT_EXPIRED_IN = 3600 * 24 * 7;

    private final String identity;

    @Override
    public Object getRequestBody(BaseDynAuthContent authContent) {
        return authContent.getParams();
    }

    @Override
    public Map<String, String> getResponseMap(BaseDynAuthContent authContent) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(TokenConst.TOKEN_KEY, authContent.getTokenField());
        responseMap.put(TokenConst.ISSUANCE_KEY, authContent.getIssuanceField());
        responseMap.put(TokenConst.EXPIRED_IN_KEY, authContent.getExpiredInField());
        return responseMap;
    }

    @Override
    public FcTokenPak mapToToken(Map<String, Object> resultMap) {
        String tokenStr = getToken(resultMap);
        LocalDateTime issuance = getIssuance(resultMap);
        LocalDateTime estimatedExpiration = getEstimatedExpiration(resultMap, issuance);
        return FcTokenPak.builder()
                .token(tokenStr)
                .issuance(issuance)
                .estimatedExpiration(estimatedExpiration)
                .build();
    }

    public LocalDateTime getEstimatedExpiration(Map<String, Object> resultMap, LocalDateTime issuance) {
        Object expiredIn = resultMap.get(TokenConst.EXPIRED_IN_KEY);
        long expiredInNum = (Objects.isNull(expiredIn)) ? DEFAULT_EXPIRED_IN : NumberUtil.parseLong(StrUtil.toString(expiredIn));
        return issuance.plusSeconds(expiredInNum);
    }

    public LocalDateTime getIssuance(Map<String, Object> resultMap) {
        Object issuance = resultMap.get(TokenConst.ISSUANCE_KEY);
        if (issuance instanceof Date) {
            return LocalDateTime.ofInstant(((Date) issuance).toInstant(), ZoneId.systemDefault());
        }
        if (issuance instanceof LocalDateTime) {
            return (LocalDateTime) issuance;
        }
        if (issuance instanceof Long) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) issuance), ZoneId.systemDefault());
        }
        if (issuance instanceof CharSequence) {
            return LocalDateTime.parse(StrUtil.toString(issuance));
        }
        return LocalDateTime.now();
    }

    public String getToken(Map<String, Object> resultMap) {
        Object token = resultMap.get(TokenConst.TOKEN_KEY);
        if (!(token instanceof CharSequence) || StringUtils.isBlank((CharSequence) token)) {
            throw new FcUnexpectedException(String.format("FastCall-对[%s]请求认证时获取token失败，请求终止", identity));
        }
        return StrUtil.toString(token);
    }
}

package priv.szf.fastcall.core.call.auth;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.time.LocalDateTime;
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
    public FcTokenPak mapToToken(FastCallResponse<String> response, T authContent) {
        if (!response.isSuccessful()) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，code[%s], message[%s]",
                            identity, response.getCode(), response.getMessage())
            );
        }

        String data = response.getData();
        if (Objects.isNull(data)) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，响应体为空，code[%s], message[%s]",
                            identity, response.getCode(), response.getMessage())
            );
        }


        JSONObject jsonData = JSONUtil.parseObj(data);

        String tokenStr = getToken(jsonData, authContent.getTokenField());
        LocalDateTime issuance = getIssuance(jsonData, authContent.getIssuanceField());
        LocalDateTime estimatedExpiration = getEstimatedExpiration(jsonData, authContent.getExpiredInField(), issuance);
        return FcTokenPak.builder()
                .token(tokenStr)
                .issuance(issuance)
                .estimatedExpiration(estimatedExpiration)
                .build();
    }

    public LocalDateTime getEstimatedExpiration(JSONObject jsonData, String fieldPath, LocalDateTime issuance) {
        Long expiredIn = jsonData.getByPath(fieldPath, Long.class);
        long expiredInNum = (Objects.isNull(expiredIn)) ? DEFAULT_EXPIRED_IN : expiredIn;
        return issuance.plusSeconds(expiredInNum);
    }

    public LocalDateTime getIssuance(JSONObject jsonData, String fieldPath) {
        String issuance = jsonData.getByPath(fieldPath, String.class);
        if (StringUtils.isNotBlank(issuance)) {
            return LocalDateTime.parse(issuance);
        }
        return LocalDateTime.now();
    }

    public String getToken(JSONObject jsonData, String fieldPath) {
        String token = jsonData.getByPath(fieldPath, String.class);
        if (StringUtils.isBlank(token)) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，响应体[%s]中路径[%s]未找到token字段",
                            identity, jsonData, fieldPath)
            );
        }
        return token;
    }

}

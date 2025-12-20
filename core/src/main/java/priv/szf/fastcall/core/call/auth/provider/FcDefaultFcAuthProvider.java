package priv.szf.fastcall.core.call.auth.provider;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.call.auth.BaseDynAuthContent;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
public class FcDefaultFcAuthProvider<C extends BaseDynAuthContent, T> implements IFcAuthProvider<C, T> {

    private static final long DEFAULT_EXPIRED_IN = 3600 * 24 * 7;

    private final String identity;

    @Override
    public FcTokenPak<T> mapToToken(Request request, FastCallResponse<String> response, C authContent) {
        checkSuccess(response);

        String data = response.getData();
        if (Objects.isNull(data)) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，响应体为空，code[%s], message[%s]",
                            identity, response.getCode(), response.getMessage())
            );
        }


        JSONObject jsonData = JSONUtil.parseObj(data);

        T tokenStr = getToken(jsonData, authContent.getTokenField());
        LocalDateTime issuance = getIssuance(jsonData, authContent.getIssuanceField());
        LocalDateTime estimatedExpiration = getEstimatedExpiration(jsonData, authContent.getExpiredInField(), issuance);
        return FcTokenPak.<T>builder()
                .token(tokenStr)
                .issuance(issuance)
                .estimatedExpiration(estimatedExpiration)
                .build();
    }

    protected String getIdentity() {
        return identity;
    }

    protected void checkSuccess(FastCallResponse<String> response) {
        if (!response.isSuccessful()) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，code[%s], message[%s], data[%s]",
                            getIdentity(), response.getCode(), response.getMessage(), response.getData())
            );
        }
    }

    protected LocalDateTime getEstimatedExpiration(JSONObject jsonData, String fieldPath, LocalDateTime issuance) {
        Long expiredIn = jsonData.getByPath(fieldPath, Long.class);
        long expiredInNum = (Objects.isNull(expiredIn)) ? DEFAULT_EXPIRED_IN : expiredIn;
        return issuance.plusSeconds(expiredInNum);
    }

    protected LocalDateTime getIssuance(JSONObject jsonData, String fieldPath) {
        String issuance = jsonData.getByPath(fieldPath, String.class);
        if (StringUtils.isNotBlank(issuance)) {
            if (NumberUtil.isLong(issuance)) {
                long issuanceLong = NumberUtil.parseLong(issuance);
                return DateUtil.date(issuanceLong).toLocalDateTime();
            }
            return LocalDateTimeUtil.parse(issuance);
        }
        return LocalDateTime.now();
    }

    protected T getToken(JSONObject jsonData, String fieldPath) {
        String token = jsonData.getByPath(fieldPath, String.class);
        if (StringUtils.isBlank(token)) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，响应体[%s]中路径[%s]未找到token字段",
                            getIdentity(), jsonData, fieldPath)
            );
        }
        return (T) token;
    }

}

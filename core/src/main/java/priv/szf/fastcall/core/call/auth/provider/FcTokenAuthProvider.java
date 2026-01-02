package priv.szf.fastcall.core.call.auth.provider;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.FastCallClient;
import priv.szf.fastcall.core.call.FastCallClientFactory;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.FcMediaType;
import priv.szf.fastcall.core.common.FcRequestMethod;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.auth.FcAuthProp;
import priv.szf.fastcall.core.model.auth.credential.TokenCredential;
import priv.szf.fastcall.core.model.auth.TokenAuthContent;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FcTokenAuthProvider extends FcBaseInteractiveAuthProvider<TokenAuthContent>
        implements IFcAuthProvider<TokenAuthContent> {

    private static final long DEFAULT_EXPIRED_IN = 3600 * 24 * 7;

    private final IFcSource source;

    @Override
    protected IFcSource getSource() {
        return source;
    }

    @Override
    protected TokenCredential buildCredential(TokenAuthContent authContent,
                                              Request request,
                                              Response response,
                                              String system) {
        FcSourcePak sourcePak = getSource().getSourcePak(system);
        FcAuthPak authPak = sourcePak.getAuth();

        FcAuthProp authProp = Optional.ofNullable(authContent.getProp()).orElse(new FcAuthProp());
        FastCallClient client = FastCallClientFactory.getExistedClient(system);
        FastCallResponse<String> authResponse = client.newCall(String.class)
                .host(authPak.getParticularHost())
                .uri(authPak.getPath())
                .params(authProp.getParams())
                .method(FcRequestMethod.POST)
                .header(FcHttpHeader.CONTENT_TYPE, FcMediaType.APPLICATION_JSON)
                .header(FcHttpHeader.ACCEPT, FcMediaType.APPLICATION_JSON)
                .headers(authProp.getHeaders())
                .body(authProp.getBody())
                .prepared()
                .anonymousCallIt();

        return mapToToken(authResponse, authContent);
    }

    private TokenCredential mapToToken(FastCallResponse<String> response, TokenAuthContent authContent) {
        checkSuccess(response);

        String data = response.getData();
        if (Objects.isNull(data)) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统刷新认证失败，响应体为空，code[%s], message[%s]",
                            response.getCode(), response.getMessage())
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

    private void checkSuccess(FastCallResponse<String> response) {
        if (!response.isSuccessful()) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统刷新认证失败，code[%s], message[%s], data[%s]",
                            response.getCode(), response.getMessage(), response.getData())
            );
        }
    }

    private LocalDateTime getEstimatedExpiration(JSONObject jsonData, String fieldPath, LocalDateTime issuance) {
        Long expiredIn = jsonData.getByPath(fieldPath, Long.class);
        long expiredInNum = (Objects.isNull(expiredIn)) ? DEFAULT_EXPIRED_IN : expiredIn;
        return issuance.plusSeconds(expiredInNum);
    }

    private LocalDateTime getIssuance(JSONObject jsonData, String fieldPath) {
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

    private String getToken(JSONObject jsonData, String fieldPath) {
        String token = jsonData.getByPath(fieldPath, String.class);
        if (StringUtils.isBlank(token)) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统刷新认证失败，响应体[%s]中路径[%s]未找到token字段",
                            jsonData, fieldPath)
            );
        }
        return token;
    }

}

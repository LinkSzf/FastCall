package priv.szf.fastcall.api.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.model.content.BaseAuthContent;

import javax.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApiKeyAuthDTO.class, name = "APIKEY"),
        @JsonSubTypes.Type(value = DigestAuthDTO.class, name = "DIGEST"),
        @JsonSubTypes.Type(value = BasicAuthDTO.class, name = "BASIC"),
        @JsonSubTypes.Type(value = BearerAuthDTO.class, name = "BEARER"),
        @JsonSubTypes.Type(value = CookieAuthDTO.class, name = "COOKIE")
})
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseAuthContentDTO extends BaseAuthContent {

    @NotNull(message = "认证类型不能为空")
    private FcAuthType type;

}

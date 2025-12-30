package priv.szf.fastcall.core.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

import javax.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NoneAuthDTO.class, name = "NONE"),
        @JsonSubTypes.Type(value = ApiKeyAuthDTO.class, name = "APIKEY"),
        @JsonSubTypes.Type(value = DigestAuthDTO.class, name = "DIGEST"),
        @JsonSubTypes.Type(value = BasicAuthDTO.class, name = "BASIC"),
        @JsonSubTypes.Type(value = BearerAuthDTO.class, name = "BEARER")
})
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseAuthContentDTO extends BaseAuthContent {

    @NotNull(message = "认证类型不能为空")
    private AuthType type;

}

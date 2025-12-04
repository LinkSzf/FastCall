package priv.szf.fastcall.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.dto.ApiKeyAuth;
import priv.szf.fastcall.core.model.dto.BasicAuth;
import priv.szf.fastcall.core.model.dto.BearerAuth;
import priv.szf.fastcall.core.model.dto.NoneAuth;
import priv.szf.fastcall.core.model.dto.TokenAuth;

import javax.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NoneAuth.class, name = "NONE"),
        @JsonSubTypes.Type(value = ApiKeyAuth.class, name = "APIKEY"),
        @JsonSubTypes.Type(value = TokenAuth.class, name = "TOKEN"),
        @JsonSubTypes.Type(value = BasicAuth.class, name = "BASIC"),
        @JsonSubTypes.Type(value = BearerAuth.class, name = "BEARER")
})
@Data
public abstract class BaseAuthContent {

    @NotNull(message = "认证类型不能为空")
    private AuthType type;

}

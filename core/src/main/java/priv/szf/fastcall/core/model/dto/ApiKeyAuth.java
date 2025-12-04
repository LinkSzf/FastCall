package priv.szf.fastcall.core.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.core.common.ParamPos;
import priv.szf.fastcall.core.model.BaseAuthContent;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiKeyAuth extends BaseAuthContent {

    @NotNull(message = "key不能为空")
    private String key;

    @NotNull(message = "value不能为空")
    private String value;

    @NotNull(message = "addTo不能为空")
    private ParamPos addTo;

}

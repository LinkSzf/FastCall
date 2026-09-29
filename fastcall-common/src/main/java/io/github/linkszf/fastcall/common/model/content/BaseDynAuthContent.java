package io.github.linkszf.fastcall.common.model.content;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseDynAuthContent extends BaseAuthContent {

    private FcAuthProp prop;

}

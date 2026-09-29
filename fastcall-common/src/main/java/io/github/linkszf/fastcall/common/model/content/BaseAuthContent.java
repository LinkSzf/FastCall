package io.github.linkszf.fastcall.common.model.content;

import lombok.Data;
import io.github.linkszf.fastcall.common.FcAuthType;

@Data
public abstract class BaseAuthContent {

    private FcAuthType type;

}

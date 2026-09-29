package io.github.linkszf.fastcall.api.model.vo;

import lombok.Data;

@Data
public class FcRetryVO {

    private Long id;

    private Long sysId;

    private Integer attempts;

    private Long duration;

}

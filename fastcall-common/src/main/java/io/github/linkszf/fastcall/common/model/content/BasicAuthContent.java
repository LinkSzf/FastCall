package io.github.linkszf.fastcall.common.model.content;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
public class BasicAuthContent extends BaseAuthContent implements Serializable {

    private static final long serialVersionUID = 2858793833097616240L;

    private String username;

    private String password;

}

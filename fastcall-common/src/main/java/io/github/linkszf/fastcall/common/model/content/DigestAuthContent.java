package io.github.linkszf.fastcall.common.model.content;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class DigestAuthContent extends BaseDynAuthContent implements Serializable {

    private static final long serialVersionUID = 2400048029857300330L;

    private String username;

    private String password;

}

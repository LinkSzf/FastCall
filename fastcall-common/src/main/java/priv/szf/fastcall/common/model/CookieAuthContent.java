package priv.szf.fastcall.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class CookieAuthContent extends BaseDynAuthContent implements Serializable {

    private static final long serialVersionUID = -427828953825647432L;
}
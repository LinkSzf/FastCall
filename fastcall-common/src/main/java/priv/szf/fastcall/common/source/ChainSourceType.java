package priv.szf.fastcall.common.source;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChainSourceType {

    REDIS(1),

    IN_MEMORY(2),

    PROPERTY(3),

    DATABASE(4);


    private final int priority;

}

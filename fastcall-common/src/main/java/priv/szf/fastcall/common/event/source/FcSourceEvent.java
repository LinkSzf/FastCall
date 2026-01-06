package priv.szf.fastcall.common.event.source;

import cn.hutool.core.util.TypeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.common.event.FcBaseEvent;
import priv.szf.fastcall.common.event.IFcEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcSourceEvent<T> extends FcBaseEvent implements IFcEvent {

    private final Class<T> entityType = (Class<T>) TypeUtil.getTypeArgument(this.getClass());;

    private final Long entityId;


    @Override
    public String getEventType() {
        return "SOURCE";
    }


}

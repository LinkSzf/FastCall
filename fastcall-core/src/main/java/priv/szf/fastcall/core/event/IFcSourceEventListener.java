package priv.szf.fastcall.core.event;

import priv.szf.fastcall.common.event.IFcEventListener;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;

/**
 * Extension point for listeners of {@code IFcSourceEvent} raised when system or api config changes.
 * The default listener invalidates source caches and drops the client on update or delete.
 */
public interface IFcSourceEventListener extends IFcEventListener<IFcSourceEvent> {
}

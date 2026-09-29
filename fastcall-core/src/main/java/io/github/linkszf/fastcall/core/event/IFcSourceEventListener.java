package io.github.linkszf.fastcall.core.event;

import io.github.linkszf.fastcall.common.event.IFcEventListener;
import io.github.linkszf.fastcall.common.event.source.IFcSourceEvent;

/**
 * Extension point for listeners of {@code IFcSourceEvent} raised when system or api config changes.
 * The default listener invalidates source caches and drops the client on update or delete.
 */
public interface IFcSourceEventListener extends IFcEventListener<IFcSourceEvent> {
}

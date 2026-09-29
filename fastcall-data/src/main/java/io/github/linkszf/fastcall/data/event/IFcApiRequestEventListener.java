package io.github.linkszf.fastcall.data.event;

import io.github.linkszf.fastcall.common.event.IFcEventListener;
import io.github.linkszf.fastcall.common.event.request.IFcApiRequestEvent;

/**
 * Extension point for listeners of the {@code IFcApiRequestEvent} raised after an api call.
 * The default {@code FcApiRequestEventListener} updates the api last access time on success.
 */
public interface IFcApiRequestEventListener extends IFcEventListener<IFcApiRequestEvent> {
}

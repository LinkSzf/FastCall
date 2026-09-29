package io.github.linkszf.fastcall.data.event;

import io.github.linkszf.fastcall.common.event.IFcEventListener;
import io.github.linkszf.fastcall.common.event.request.IFcAuthRequestEvent;

/**
 * Extension point for listeners of the {@code IFcAuthRequestEvent} raised after an authentication call.
 * The default {@code FcAuthRequestEventListener} updates the auth last access time on success.
 */
public interface IFcAuthRequestEventListener extends IFcEventListener<IFcAuthRequestEvent> {
}


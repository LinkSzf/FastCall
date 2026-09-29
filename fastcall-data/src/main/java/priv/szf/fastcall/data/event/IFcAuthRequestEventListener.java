package priv.szf.fastcall.data.event;

import priv.szf.fastcall.common.event.IFcEventListener;
import priv.szf.fastcall.common.event.request.IFcAuthRequestEvent;

/**
 * Extension point for listeners of the {@code IFcAuthRequestEvent} raised after an authentication call.
 * The default {@code FcAuthRequestEventListener} updates the auth last access time on success.
 */
public interface IFcAuthRequestEventListener extends IFcEventListener<IFcAuthRequestEvent> {
}


package priv.szf.fastcall.data.event;

import priv.szf.fastcall.common.event.IFcEventListener;
import priv.szf.fastcall.common.event.request.IFcApiRequestEvent;

/**
 * Extension point for listeners of the {@code IFcApiRequestEvent} raised after an api call.
 * The default {@code FcApiRequestEventListener} updates the api last access time on success.
 */
public interface IFcApiRequestEventListener extends IFcEventListener<IFcApiRequestEvent> {
}

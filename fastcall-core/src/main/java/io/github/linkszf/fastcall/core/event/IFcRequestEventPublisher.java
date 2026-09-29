package io.github.linkszf.fastcall.core.event;

import io.github.linkszf.fastcall.common.event.IFcEventPublisher;

/**
 * Extension point for publishing the request events raised by {@code FcRequestEventFilter}.
 * Only registered when request events are enabled; a user bean replaces the asynchronous default.
 */
public interface IFcRequestEventPublisher extends IFcEventPublisher {
}

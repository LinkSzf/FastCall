package io.github.linkszf.fastcall.common.event.request;

import io.github.linkszf.fastcall.common.event.IFcEvent;

public interface IFcRequestEvent extends IFcEvent {

    String getUrl();

    boolean isSuccess();


}

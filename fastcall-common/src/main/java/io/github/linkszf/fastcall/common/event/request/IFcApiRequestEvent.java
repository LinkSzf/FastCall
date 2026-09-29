package io.github.linkszf.fastcall.common.event.request;

public interface IFcApiRequestEvent extends IFcAuthRequestEvent {

    String getSystem();

    String getApi();
}

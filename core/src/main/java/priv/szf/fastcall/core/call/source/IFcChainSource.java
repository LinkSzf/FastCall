package priv.szf.fastcall.core.call.source;

public interface IFcChainSource extends IFcSource {

    int getWeight();

    void setNextSource(IFcSource source);

    IFcSource getNextSource();

    default void init() {}
}

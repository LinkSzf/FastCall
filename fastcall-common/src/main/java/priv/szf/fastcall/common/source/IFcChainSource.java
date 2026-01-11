package priv.szf.fastcall.common.source;

public interface IFcChainSource extends IFcSource {

    int getWeight();

    void setNextSource(IFcSource source);

    IFcSource getNextSource();

    default void init() {}
}

package priv.szf.fastcall.common.source;

public interface IFcChainSource extends IFcSource {

    ChainSourceType getType();

    void setNextSource(IFcSource source);

    IFcSource getNextSource();

    default void init() {}
}

package priv.szf.fastcall.common.source;

public interface IFcChainSource extends IFcSource {

    void setNextSource(IFcSource source);

    IFcSource getNextSource();
}

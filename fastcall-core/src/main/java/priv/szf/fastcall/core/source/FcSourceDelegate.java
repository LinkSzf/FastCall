package priv.szf.fastcall.core.source;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcChainSource;
import priv.szf.fastcall.common.source.IFcSource;

import java.util.List;

@RequiredArgsConstructor
public class FcSourceDelegate implements IFcSource {

    private final List<IFcChainSource> chainSources;

    private IFcSource source;

    @Override
    public void init() {
        if (CollectionUtil.isEmpty(this.chainSources)) {
            throw new FcUnexpectedException("No available sources");
        }

        AnnotationAwareOrderComparator.sort(this.chainSources);

        IFcChainSource last = null;
        for (IFcChainSource source : this.chainSources) {
            source.setNextSource(last);
            last = source;
            source.init();
        }

        this.source = last;
    }

    @Override
    public FcSourcePak getSourcePak(String system) {
        FcSourcePak sourcePak = this.source.getSourcePak(system);
        FcSourcePak cloned = ObjectUtil.clone(sourcePak);
        cloned.setCredential(sourcePak.getCredential());
        return cloned;
    }

    @Override
    public void updateCredential(String system, ICredential credential) {
        this.source.updateCredential(system, credential);
    }
}

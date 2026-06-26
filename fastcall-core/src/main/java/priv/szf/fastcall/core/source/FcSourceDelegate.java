package priv.szf.fastcall.core.source;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcChainSource;
import priv.szf.fastcall.common.source.IFcSource;

import java.util.Collections;
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
        Collections.reverse(this.chainSources);

        IFcChainSource last = null;
        for (IFcChainSource source : this.chainSources) {
            source.setNextSource(last);
            source.init();
            last = source;
        }

        this.source = last;
    }

    @Override
    public FcSourcePak getSourcePak(String system) {
        return this.source.getSourcePak(system);
    }

    @Override
    public void updateCredential(String system, ICredential credential) {
        this.source.updateCredential(system, credential);
    }
}

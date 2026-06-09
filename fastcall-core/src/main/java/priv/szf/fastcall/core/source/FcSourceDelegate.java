package priv.szf.fastcall.core.source;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcChainSource;
import priv.szf.fastcall.common.source.IFcSource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FcSourceDelegate implements IFcSource {

    private final IFcSource source;

    @Autowired
    public FcSourceDelegate(List<IFcChainSource> availableSources) {
        if (CollectionUtil.isEmpty(availableSources)) {
            throw new FcUnexpectedException("No available sources");
        }

        List<IFcChainSource> sortedSources = availableSources.stream()
                .sorted(Comparator.comparingInt(IFcChainSource::getWeight))
                .collect(Collectors.toList());

        IFcChainSource previous = null;
        for (IFcChainSource sortedSource : sortedSources) {
            sortedSource.setNextSource(previous);
            previous = sortedSource;
            sortedSource.init();
        }

        this.source = previous;
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

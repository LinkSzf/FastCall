package priv.szf.fastcall.core.source;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcChainSource;
import priv.szf.fastcall.common.source.IFcSource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Primary
@Component
public class FcSourceDelegate extends FcBaseChainSource implements IFcSource {

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
        this.setNextSource(previous);
    }

    @Override
    public int getWeight() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected FcSourcePak tryGetSourcePak(String systemCode) {
        return null;
    }

    @Override
    protected void tryUpdateCredential(String system, ICredential credential) {
    }


}

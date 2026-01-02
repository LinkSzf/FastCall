package priv.szf.fastcall.core.call.source;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.auth.credential.ICredential;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Primary
@Component
public class FcSourceDelegate extends FcBaseChainSource implements IFcSource {

    @Autowired
    public FcSourceDelegate(List<IFcChainSource> availableSources) {
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
    protected FcSourcePak tryGetSourcePak(String systemCode) {
        return null;
    }

    @Override
    protected void tryUpdateCredential(String system, ICredential credential) {
    }

    @Override
    public int getWeight() {
        return Integer.MAX_VALUE;
    }

}

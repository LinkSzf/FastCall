package priv.szf.fastcall.core.call.source;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Primary
@Component
public class FcSourceDelegate extends FcBaseSource implements IFcSource {

    @Autowired
    public FcSourceDelegate(List<IFcSource> availableSources) {
        List<IFcSource> sortedSources = availableSources.stream()
                .sorted(Comparator.comparingInt(IFcSource::getWeight))
                .collect(Collectors.toList());

        IFcSource previous = null;
        for (IFcSource sortedSource : sortedSources) {
            sortedSource.setNextSource(previous);
            previous = sortedSource;
            sortedSource.init();
        }
        this.setNextSource(previous);
    }

    @Override
    public FcSourcePak getSourcePak(String systemCode) {
        return getNextSource().getSourcePak(systemCode);
    }

    @Override
    public <T> void updateAccessToken(String systemCode, FcTokenPak<T> token) {
        getNextSource().updateAccessToken(systemCode, token);
    }

    @Override
    public int getWeight() {
        return Integer.MAX_VALUE;
    }

}

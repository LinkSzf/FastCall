package priv.szf.fastcall.core.call;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.source.IFcSource;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastCallClient {

    private final IFcSource source;


    public <T> FastCallResult<T> call() {
        return null;
    }




}

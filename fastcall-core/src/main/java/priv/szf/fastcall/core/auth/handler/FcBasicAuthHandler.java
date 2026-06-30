package priv.szf.fastcall.core.auth.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcBasicCredentialProvider;
import priv.szf.fastcall.common.FcAuthType;

@RequiredArgsConstructor
@Component
public class FcBasicAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {

    @Getter
    private final FcBasicCredentialProvider credentialProvider;

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.BASIC;
    }


}

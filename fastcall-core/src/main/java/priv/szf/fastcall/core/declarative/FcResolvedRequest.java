package priv.szf.fastcall.core.declarative;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
final class FcResolvedRequest {

    private final String system;

    private final FcRequestMethod method;

    private final String host;

    private final String uri;

    private final Map<String, List<String>> headers;

    private final Map<String, List<String>> queries;

    private final Object body;

    private final boolean hasBody;

    private final FcMediaType bodyType;
}

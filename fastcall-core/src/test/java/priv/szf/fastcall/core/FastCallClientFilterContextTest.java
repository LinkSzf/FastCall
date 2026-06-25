package priv.szf.fastcall.core;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.common.filter.FcFilter;
import priv.szf.fastcall.common.filter.FcFilterChain;
import priv.szf.fastcall.common.filter.FcFilterContext;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.filter.FcFilterManager;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FastCallClientFilterContextTest {

    @Test
    void should_fill_call_type_and_response_code_into_filter_context() throws Exception {
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        when(okHttpClient.newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(buildResponse(201, "Created", "{}", "application/json"));

        CaptureFilter filter = new CaptureFilter();
        FastCallClient client = buildClient(okHttpClient, filter);

        client.newCall()
                .url("http://localhost/demo")
                .prepared()
                .anonymousCallIt();

        assertEquals(FcCallType.ANONYMOUS, filter.context.getCallType());
        assertEquals(Integer.valueOf(201), filter.context.getResponseCode());
        assertNull(filter.context.getExceptionType());
    }

    @Test
    void should_fill_exception_type_into_filter_context_for_non_throwing_failure() throws Exception {
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        when(okHttpClient.newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("network down"));

        CaptureFilter filter = new CaptureFilter();
        FastCallClient client = buildClient(okHttpClient, filter);

        client.newCall()
                .url("http://localhost/demo")
                .prepared()
                .callIt();

        assertEquals(FcCallType.NORMAL, filter.context.getCallType());
        assertNull(filter.context.getResponseCode());
        assertEquals(IOException.class, filter.context.getExceptionType());
        assertInstanceOf(IOException.class, filter.context.getThrowable());
    }

    private FastCallClient buildClient(OkHttpClient okHttpClient, CaptureFilter filter) {
        return FastCallClient.builder()
                .client(okHttpClient)
                .system("demo")
                .authType(FcAuthType.NONE)
                .source(new FixedSource(buildSourcePak()))
                .filterManager(new FcFilterManager(Collections.singletonList(filter)))
                .build();
    }

    private FcSourcePak buildSourcePak() {
        FcSystemPak systemPak = new FcSystemPak();
        systemPak.setCode("demo");
        systemPak.setHost("http://localhost");
        systemPak.setAuthType(FcAuthType.NONE);
        return FcSourcePak.builder()
                .system(systemPak)
                .build();
    }

    private Response buildResponse(int code, String message, String body, String mediaType) {
        Request request = new Request.Builder()
                .url("http://localhost/demo")
                .build();
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(message)
                .body(ResponseBody.create(body, MediaType.get(mediaType)))
                .build();
    }

    private static final class FixedSource implements IFcSource {

        private final FcSourcePak sourcePak;

        private FixedSource(FcSourcePak sourcePak) {
            this.sourcePak = sourcePak;
        }

        @Override
        public FcSourcePak getSourcePak(String system) {
            return sourcePak;
        }

        @Override
        public void updateCredential(String system, ICredential credential) {
        }
    }

    private static final class CaptureFilter implements FcFilter {

        private FcFilterContext context;

        @Override
        public void doFilter(FcFilterContext context, FcFilterChain chain) {
            chain.doFilter(context);
            this.context = context;
        }
    }
}

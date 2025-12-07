package priv.szf.fastcall.core.call;

import lombok.AllArgsConstructor;
import okhttp3.*;
import okio.BufferedSink;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import priv.szf.fastcall.core.call.auth.FcCallType;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.io.IOException;

@AllArgsConstructor
public class FastCallClient {

    private final OkHttpClient client;

    private final FcSourcePak source;

//    private final FcSystemPak system;
//
//    private final FcAuthPak auth;
//
//    private final FcTokenPak token;

    private final ThreadLocal<FcApiPak> api = new ThreadLocal<>();

//    public FastCallClient(OkHttpClient client, FcSystemPak system, FcAuthPak auth, FcTokenPak accessToken) {
//        this.client = client;
//        this.system = system;
//        this.auth = auth;
//        this.token = accessToken;
//    }

//    public FastCallClient(OkHttpClient client, FcSourcePak source) {
//        this.client = client;
//        this.source = source;
//    }

    public <T> FastCallResult<T> call() {
        Request request = new Request.Builder()
                .url("https://api.example.com/protected-resource")
                .tag(FcSourcePak.class, source)
                .tag(AuthType.class, source.getAuth().getType())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FastCallClient withApi(FcApiPak apiPak) {
        this.api.set(apiPak);
        return this;
    }

    public FcTokenPak doAuth() {
        FcAuthPak auth = source.getAuth();
        FcSystemPak system = source.getSystem();
        String host = StringUtils.isBlank(auth.getParticularHost()) ? system.getHost() : auth.getParticularHost();
        String url = host + auth.getPath();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(new RequestBody() {
                    @Nullable
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {

                    }
                })
                .tag(FcCallType.class, FcCallType.AUTH)
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            ResponseBody body = response.body();


        } catch (IOException e) {

        }

        return null;
    }



}

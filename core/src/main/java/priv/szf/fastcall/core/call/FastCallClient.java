package priv.szf.fastcall.core.call;

import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.FcApiPak;

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

    public FastCallClient(OkHttpClient client, FcSourcePak source) {
        this.client = client;
        this.source = source;
    }

    public <T> FastCallResult<T> call() {
//        String getTicket = AuthProvider.getTicket();


        Request request = new Request.Builder()
                .url("https://api.example.com/protected-resource")
                .tag(FcSourcePak.class, source)
                .tag(AuthType.class, source.getAuth().getType())
//                .header("Authorization", basicAuth)
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




}

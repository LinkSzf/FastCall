package priv.szf.fastcall.core.call;

import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.call.source.FcSourcePak;

import java.io.IOException;

@AllArgsConstructor
public class FastCallClient {

    private final OkHttpClient client;

    private final FcSystemPak system;

    private final FcAuthPak auth;

    private final ThreadLocal<FcApiPak> api = new ThreadLocal<>();


    public FastCallClient(OkHttpClient client, FcSourcePak source) {
        this.client = client;
        this.system = source.getSystem();
        this.auth = source.getAuth();
    }

    public <T> FastCallResult<T> call() {
//        String getTicket = AuthProvider.getTicket();


        Request request = new Request.Builder()
                .url("https://api.example.com/protected-resource")
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

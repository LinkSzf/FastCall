package priv.szf.fastcall.core.call;

import okhttp3.OkHttpClient;

public class FastCallClient {

    private final OkHttpClient okHttpClient;

    public FastCallClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public <T> FastCallResult<T> call() {
        return null;
    }




}

package it.alessioricco.btc.api;

import android.util.Log;

import java.io.IOException;


public class HttpClientFactory {

    public static okhttp3.OkHttpClient create() {
        return new okhttp3.OkHttpClient()
                .newBuilder()
                .addInterceptor(new okhttp3.Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Request request = chain.request();
                        Log.i("rest", request.url().toString());
                        return chain.proceed(chain.request());
                    }
                }).build();
    }

}

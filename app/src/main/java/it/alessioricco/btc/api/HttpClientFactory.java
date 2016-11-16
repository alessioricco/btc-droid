package it.alessioricco.btc.api;

import android.app.Application;
import android.app.usage.NetworkStats;
import android.content.Context;
import android.util.Log;

import java.io.IOException;

import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.utils.NetworkStatus;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpClientFactory {

    final Context context;

    public HttpClientFactory(Context context) {
        ObjectGraphSingleton.getInstance().inject(this);
        this.context = context;
    }

    //http://stackoverflow.com/questions/23429046/can-retrofit-with-okhttp-use-cache-data-when-offline
    private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());

            if (context != null && NetworkStatus.isInternetConnected(context) != NetworkStatus.NOCONNECTION) {
                int maxAge = 60; // read from cache for 1 minute
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();

            }
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();

        }
    };

    public okhttp3.OkHttpClient create() {
        return create(false);
    }

    public okhttp3.OkHttpClient create(final Boolean cache) {
        OkHttpClient.Builder clientBuilder =new OkHttpClient()
                .newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Log.i("rest", request.url().toString());
                        return chain.proceed(chain.request());
                    }
                });
        if (cache) {
            // in test this will never works so we need to shadow it
            clientBuilder.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        }
       return clientBuilder.build();
    }

}

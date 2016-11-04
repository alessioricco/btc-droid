package it.alessioricco.btc.api;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.inject.Inject;

import it.alessioricco.btc.injection.ObjectGraphSingleton;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Created by alessioricco on 02/10/2016.
 *
 * http://blog.andresteingress.com/2014/09/08/android-rest-retrofit/
 *
 * TODO: need for better refactoring for those builders
 */

public final class RestAdapterFactory {

    final private String url = "http://api.bitcoincharts.com";

    public RestAdapterFactory() {
        ObjectGraphSingleton.getInstance().inject(this);
    }

    //todo: we can inject it
    private okhttp3.OkHttpClient createClient() {
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

    // todo: refactor to be a class and not a method
    public Retrofit getJSONRestAdapter() {

        final RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .client(createClient())
                .build();

    }

    // todo: refactor to be a class and not a method
    public Retrofit getRawRestAdapter() {

        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(new ToStringConverterFactory())
                .client(createClient())
                .build();

    }

}

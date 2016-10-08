package it.alessioricco.btc.api;

import com.squareup.okhttp.OkHttpClient;

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

    @Inject OkHttpClient httpClient;

    final String url = "http://api.bitcoincharts.com";

    public RestAdapterFactory() {
        ObjectGraphSingleton.getInstance().inject(this);
    }

    // todo: refactor to be a class and not a method
    public Retrofit getJSONRestAdapter() {

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

    }

    // todo: refactor to be a class and not a method
    public Retrofit getRawRestAdapter() {

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(new ToStringConverterFactory())
                .build();

    }

}

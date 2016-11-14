package it.alessioricco.btc.api;

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

public class RestAdapterFactory {

    protected String getBaseUrl() {
        return "http://api.bitcoincharts.com";
    }

    public RestAdapterFactory() {
        ObjectGraphSingleton.getInstance().inject(this);
    }


    /**
     * Rest adapter for JSON feed (bitcoincharts)
     * @return
     */
    public Retrofit getJSONRestAdapter() {

        final RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .client(HttpClientFactory.create())
                .build();
    }

    /**
     * Rest adapter for CVS feed (bitcoincharts history)
     * @return
     */
     public Retrofit getRawRestAdapter() {
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .client(HttpClientFactory.create())
                .build();

    }

    /**
     * Rest adapter for CVS feed (generic feed rss)
     * @return
     */
    public Retrofit getRssRestAdapter() {
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                //.addConverterFactory(new ToStringConverterFactory())
                .client(HttpClientFactory.create())
                .build();

    }
}

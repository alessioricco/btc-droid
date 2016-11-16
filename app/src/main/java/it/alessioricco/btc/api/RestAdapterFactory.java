package it.alessioricco.btc.api;

import javax.inject.Inject;

import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.utils.Environment;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Adapter per retrofit
 *
 * http://blog.andresteingress.com/2014/09/08/android-rest-retrofit/
 *
 * TODO: need for better refactoring for those builders
 */

public class RestAdapterFactory {

    @Inject
    HttpClientFactory httpClientFactory;

    String getBaseUrl() {
        return Environment.marketsUrl;
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
                .client(httpClientFactory.create(false))
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
                .client(httpClientFactory.create(false))
                .build();

    }


}

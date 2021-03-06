package it.alessioricco.btc.api;

import javax.inject.Inject;

import it.alessioricco.btc.injection.ObjectGraphSingleton;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class RssAdapterFactory {

    @Inject
    HttpClientFactory httpClientFactory;

    public RssAdapterFactory() {
        ObjectGraphSingleton.getInstance().inject(this);
    }

    /**
     * Rest adapter for CVS feed (generic feed rss)
     * @return
     */
    public Retrofit getRssRestAdapter(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // RxJava adapter
                .addConverterFactory(SimpleXmlConverterFactory.create()) // Simple XML converter
                .client(httpClientFactory.create(true))
                .build();

    }

}

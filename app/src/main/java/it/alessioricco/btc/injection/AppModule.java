package it.alessioricco.btc.injection;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.alessioricco.btc.BtcTickerApp;
import it.alessioricco.btc.MainActivity;
import it.alessioricco.btc.api.HttpClientFactory;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.services.MarketsService;
import retrofit2.Retrofit;


/**
 * Created by alessioricco on 02/10/2016.
 *
 * The class providing all the factories to be used for dependency injection with dagger
 */

@Module(
        injects = {
                BtcTickerApp.class,
                MarketsService.class,

                RestAdapterFactory.class,

                MainActivity.class
        },
        library = true)
public class AppModule {

    private BtcTickerApp app; // App: constructor
    private BtcTickerApp testApp; // Test: constructor and environment

    /**
     * constructor for the main android app
     * @param app the application itself
     */
    public AppModule(BtcTickerApp app) {
        this.app = app;
    }

    /**
     * constructor for unit test
     */
    public AppModule() {
        try {
            if (testApp == null) {
                testApp = new BtcTickerApp();
            }
            app = testApp;
        } catch (Exception e)
        {/* do nothing */}
    }

    /**
     * Markets Service factory
     * @return a well formed marketsService object
     */
    @Provides
    public @Singleton
    MarketsService provideMarketsService() {
        return new MarketsService();
    }

    /**
     * RestAdapter factory
     * used to build a restadapter for the default ticker service endpoint
     * @return a well formed RestAdapter object
     */
    @Provides @Singleton public Retrofit provideRestAdapter() {
        return new RestAdapterFactory().getJSONRestAdapter();
    }

    @Provides @Singleton public OkHttpClient providesOkHttpClient() {
        return new HttpClientFactory().getHttpClient();
    }
}

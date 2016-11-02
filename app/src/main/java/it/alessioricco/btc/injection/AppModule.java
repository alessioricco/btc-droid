package it.alessioricco.btc.injection;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.alessioricco.btc.App;
import it.alessioricco.btc.MainActivity;
import it.alessioricco.btc.api.HttpClientFactory;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.services.MarketsService;


/**
 * Created by alessioricco on 02/10/2016.
 *
 * The class providing all the factories to be used for dependency injection with dagger
 */

@Module(
        injects = {
                App.class,
                MarketsService.class,

                RestAdapterFactory.class,

                MainActivity.class
        },
        library = true)

public class AppModule {

    private App app; // App: constructor
    private App testApp; // Test: constructor and environment

    /**
     * constructor for the main android app
     * @param app the application itself
     */
    public AppModule(App app) {
        this.app = app;
    }

    /**
     * constructor for unit test
     */
    public AppModule() {
        try {
            if (testApp == null) {
                testApp = new App();
            }
            app = testApp;
        } catch (Exception e)
        {/* do nothing */}
    }

    @Provides @Singleton public Context provideContext() {
        return app;
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
     * @return a well formed RestAdapterFactory object
     */
    @Provides @Singleton public RestAdapterFactory provideRestAdapter() {
        return new RestAdapterFactory();
    }

    @Provides @Singleton public OkHttpClient providesOkHttpClient() {
        return new HttpClientFactory().getHttpClient();
    }
}

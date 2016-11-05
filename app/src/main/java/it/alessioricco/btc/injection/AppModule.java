package it.alessioricco.btc.injection;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.alessioricco.btc.App;
import it.alessioricco.btc.MainActivity;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.fragments.HistorySample;
import it.alessioricco.btc.services.HistoryService;
import it.alessioricco.btc.services.MarketsService;
import st.lowlevel.storo.Storo;
import st.lowlevel.storo.StoroBuilder;


/**
 * Created by alessioricco on 02/10/2016.
 *
 * The class providing all the factories to be used for dependency injection with dagger
 */

@Module(
        injects = {
                App.class,
                MarketsService.class,
                HistoryService.class,
                RestAdapterFactory.class,

                HistorySample.class,

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

    @Provides
    public @Singleton
    HistoryService provideHistoryService() {
        return new HistoryService();
    }


    /**
     * RestAdapter factory
     * used to build a restadapter for the default ticker service endpoint
     * @return a well formed RestAdapterFactory object
     */
    @Provides @Singleton public RestAdapterFactory provideRestAdapter() {
        return new RestAdapterFactory();
    }

}

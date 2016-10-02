package it.alessioricco.btc.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.alessioricco.btc.BtcTickerApp;
import it.alessioricco.btc.services.MarketsService;

/**
 * Created by alessioricco on 02/10/2016.
 *
 * The class providing all the factories to be used for dependency injection with dagger
 */

@Module(
        injects = {
                BtcTickerApp.class,
                MarketsService.class
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

}

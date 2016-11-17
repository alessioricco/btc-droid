package it.alessioricco.btc.injection;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import it.alessioricco.btc.activities.TestMainActivity;
import it.alessioricco.btc.adapters.TestRSSListAdapters;
import it.alessioricco.btc.api.HttpClientFactory;
import it.alessioricco.btc.api.ShadowHttpClientFactory;
import it.alessioricco.btc.services.HistoryService;
import it.alessioricco.btc.services.ShadowHistoryService;
import it.alessioricco.btc.services.TestFeedService;
import it.alessioricco.btc.services.TestHistoryService;
import it.alessioricco.btc.services.TestMarketService;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.api.ShadowRestAdapterFactory;
import it.alessioricco.btc.mocks.MockAppWebServer;
import it.alessioricco.btc.models.TestCurrentSelection;
import it.alessioricco.btc.util.TestNetworkStatus;

import org.robolectric.shadows.ShadowApplication;

import javax.inject.Singleton;

@Module(includes = {
        AppModule.class
        },
        injects = {
                // here the list of classes using injection
                AppModule.class,
                TestMarketService.class,
                TestCurrentSelection.class,
                TestHistoryService.class,
                TestFeedService.class,
                MockAppWebServer.class,
                ShadowHistoryService.class,
                TestMainActivity.class,
                TestNetworkStatus.class,
                TestRSSListAdapters.class,
                ShadowHttpClientFactory.class,
                ShadowRestAdapterFactory.class
        },
        library = true, overrides = true)
public class TestAppModule {

    private final Context context;

    TestAppModule() {
        context = ShadowApplication.getInstance().getApplicationContext();
    }

    @Provides
    @Singleton
    public Context getContext() {
        return context;
    }


    @Provides
    @Singleton
    public MockAppWebServer getWebServer() {
        return new MockAppWebServer();
    }

    /**
     * RestAdapter factory
     * used to build a restadapter for the default ticker service endpoint
     * @return a well formed RestAdapterFactory object
     */
    @Provides @Singleton public RestAdapterFactory provideRestAdapter() {
        return new ShadowRestAdapterFactory();
    }

    @Provides @Singleton public HttpClientFactory provideHttpClientfactory() {
        return new ShadowHttpClientFactory(context);
    }

    @Provides
    public @Singleton
    HistoryService provideHistoryService() {
        return new ShadowHistoryService();
    }
}

package it.alessioricco.btc.injection;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import it.alessioricco.btc.TestMockServer;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.api.ShadowRestAdapterFactory;
import it.alessioricco.btc.models.TestCurrentSelection;
import okhttp3.mockwebserver.MockWebServer;

import org.robolectric.shadows.ShadowApplication;

import javax.inject.Singleton;

/**
 * Created by alessioricco on 02/11/2016.
 */

@Module(includes = {
        AppModule.class
        },
        injects = {
                // here the list of classes using injection
                TestMockServer.class,
                TestCurrentSelection.class,
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
    public MockWebServer getWebServer() {
        return new MockWebServer();
    }

    /**
     * RestAdapter factory
     * used to build a restadapter for the default ticker service endpoint
     * @return a well formed RestAdapterFactory object
     */
    @Provides @Singleton public RestAdapterFactory provideRestAdapter() {
        return new ShadowRestAdapterFactory();
    }
}

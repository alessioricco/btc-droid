package it.alessioricco.btc.injection;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import it.alessioricco.btc.models.TestCurrentSelection;
import it.alessioricco.btc.models.TestMarket;
import it.alessioricco.btc.models.TestHistoricalValue;

import org.robolectric.shadows.ShadowApplication;

import javax.inject.Singleton;

/**
 * Created by alessioricco on 02/11/2016.
 */

@Module(includes = {
        AppModule.class
        },
        injects = {
                TestCurrentSelection.class
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

}

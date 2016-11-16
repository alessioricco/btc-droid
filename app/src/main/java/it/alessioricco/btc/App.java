package it.alessioricco.btc;

import android.app.Application;

import java.util.Collections;
import java.util.List;

import dagger.ObjectGraph;
import it.alessioricco.btc.injection.AppModule;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import st.lowlevel.storo.Storo;
import st.lowlevel.storo.StoroBuilder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class App extends Application {

    @Override public void onCreate() {
        super.onCreate();

        // Initialize ObjectGraph for dependency Injection
        if (ObjectGraphSingleton.getInstance() == null) {
            ObjectGraph objectGraph = ObjectGraph.create(getModules().toArray());
            ObjectGraphSingleton.setInstance(objectGraph);
            objectGraph.inject(this);
        }

        // Load custom fonts
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Lato-Black.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());
        // caching
        if (!Storo.isInitialized()) {
            StoroBuilder.configure(100 * 1024)  // maximum size to allocate in bytes
                    .setDefaultCacheDirectory(this)
                    .initialize();
        }

    }


    private List<Object> getModules() {
        return Collections.<Object>singletonList(new AppModule(this));
    }

    @Override public void onTerminate() {
        super.onTerminate();
        ObjectGraphSingleton.reset();
    }

}

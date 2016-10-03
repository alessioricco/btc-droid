package it.alessioricco.btc;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import it.alessioricco.btc.injection.AppModule;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by alessioricco on 02/10/2016.
 *
 * This is the main app
 *
 */

public final class BtcTickerApp extends Application {

    @Override public void onCreate() {
        super.onCreate();

        // Initialize ObjectGraph for dependency Injection
        if (ObjectGraphSingleton.getInstance() == null) {
            ObjectGraph objectGraph = ObjectGraph.create(getModules().toArray());
            ObjectGraphSingleton.setInstance(objectGraph);
            objectGraph.inject(this);
        }


/*        // Load custom fonts
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder().setDefaultFontPath("nofont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());*/
    }

    final private List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    @Override public void onTerminate() {
        super.onTerminate();
        ObjectGraphSingleton.reset();
    }
}
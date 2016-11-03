package it.alessioricco.btc;

import android.app.Application;
import android.content.Context;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import it.alessioricco.btc.injection.AppModule;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import st.lowlevel.storo.StoroBuilder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by alessioricco on 02/10/2016.
 *
 * This is the main app
 *
 */

public final class App extends Application {

    private static Context mContext;

    @Override public void onCreate() {
        super.onCreate();
        mContext = this;

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
        //TODO: a problem with initialization must be fixed (injection?)
        StoroBuilder.configure(100*1024)  // maximum size to allocate in bytes
                .setDefaultCacheDirectory(this)
                .initialize();
    }

    //http://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context/4391811#4391811
    public static Context getContext(){
        return mContext;
    }

    final private List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    @Override public void onTerminate() {
        super.onTerminate();
        ObjectGraphSingleton.reset();
    }

}

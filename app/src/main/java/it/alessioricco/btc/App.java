package it.alessioricco.btc;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import dagger.ObjectGraph;
import it.alessioricco.btc.injection.AppModule;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import rx.functions.Action1;
import rx.plugins.RxJavaHooks;
import st.lowlevel.storo.Storo;
import st.lowlevel.storo.StoroBuilder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by alessioricco on 02/10/2016.
 *
 * This is the main app
 *
 */

public final class App extends Application {

    //todo: this is a memory leak, must be fixed
    //private static Context mContext;

    @Override public void onCreate() {
        super.onCreate();
        //mContext = this;

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
        if (!Storo.isInitialized()) {
            StoroBuilder.configure(100 * 1024)  // maximum size to allocate in bytes
                    .setDefaultCacheDirectory(this)
                    .initialize();
        }


        //rx hooks
//        RxJavaHooks.setOnError(new Action1<Throwable>() {
//            @Override
//            public void call(Throwable throwable) {
//                Log.w("RXError",throwable);
//            }
//        });
    }

    //http://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context/4391811#4391811
//    public static Context getContext(){
//        return mContext;
//    }

    private List<Object> getModules() {
        return Collections.<Object>singletonList(new AppModule(this));
    }

    @Override public void onTerminate() {
        super.onTerminate();
        ObjectGraphSingleton.reset();
    }

}

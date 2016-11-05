package it.alessioricco.btc.injection;

import dagger.ObjectGraph;

/**
 * Created by alessioricco on 02/11/2016.
 */

public class TestObjectGraphSingleton {

    static public void init() {

        final TestAppModule[] modules = {
                new TestAppModule()
        };

        ObjectGraphSingleton.reset();
        ObjectGraphSingleton.setInstance(ObjectGraph.create((Object[]) modules));
    }

    static public ObjectGraph getInstance() {
        return ObjectGraphSingleton.getInstance();
    }

    static public void reset() {
        ObjectGraphSingleton.reset();
    }
}

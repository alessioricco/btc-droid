package it.alessioricco.btc.injection;
/**
 * Created by alessioricco on 02/10/2016.
 */

import dagger.ObjectGraph;

public class ObjectGraphSingleton {
    private static ObjectGraph instance = null;

    public static ObjectGraph getInstance() {
        return ObjectGraphSingleton.instance;
    }

    public static void reset() {
        ObjectGraphSingleton.instance = null;
    }

    public static void setInstance(ObjectGraph instance) {
        if (ObjectGraphSingleton.instance == null) {
            ObjectGraphSingleton.instance = instance;
        } else {
            throw new RuntimeException("Invalid assignment: the instance has been signed already");
        }

    }
}

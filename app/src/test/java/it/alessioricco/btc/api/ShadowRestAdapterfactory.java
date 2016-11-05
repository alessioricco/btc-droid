package it.alessioricco.btc.api;

import javax.inject.Inject;

import it.alessioricco.btc.injection.TestObjectGraphSingleton;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Created by alessioricco on 05/11/2016.
 */

public class ShadowRestAdapterFactory extends RestAdapterFactory{

    @Inject
    MockWebServer mockWebServer;

    public ShadowRestAdapterFactory(){
        super();
        TestObjectGraphSingleton.getInstance().inject(this);
    }

    @Override
    protected String getBaseUrl() {
        return mockWebServer.url("").toString();
    }


}

package it.alessioricco.btc.api;

import javax.inject.Inject;

import it.alessioricco.btc.injection.TestObjectGraphSingleton;
import it.alessioricco.btc.mocks.MockAppWebServer;


public class ShadowRestAdapterFactory extends RestAdapterFactory {

    @Inject
    MockAppWebServer mockWebServer;

    public ShadowRestAdapterFactory(){
        super();
        TestObjectGraphSingleton.getInstance().inject(this);
    }

    @Override
    protected String getBaseUrl() {
        return mockWebServer.getMockWebServer().url("").toString();
    }


}

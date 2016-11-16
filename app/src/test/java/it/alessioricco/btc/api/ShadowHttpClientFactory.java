package it.alessioricco.btc.api;


import android.content.Context;

import it.alessioricco.btc.injection.TestObjectGraphSingleton;

public class ShadowHttpClientFactory extends HttpClientFactory {

    public ShadowHttpClientFactory(Context context){
        super(context);
        TestObjectGraphSingleton.getInstance().inject(this);
    }

    @Override
    public okhttp3.OkHttpClient create(final Boolean cache) {
        // overriding cache
        return super.create(false);
    }
}

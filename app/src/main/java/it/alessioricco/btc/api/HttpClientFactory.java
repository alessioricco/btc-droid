package it.alessioricco.btc.api;

import com.squareup.okhttp.OkHttpClient;


/**
 * Created by alessioricco on 02/10/2016.
 */

//TODO: remove
final public class HttpClientFactory {

    final public OkHttpClient getHttpClient() {
        return new OkHttpClient();
    }

}

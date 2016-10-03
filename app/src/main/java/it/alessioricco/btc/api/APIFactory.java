package it.alessioricco.btc.api;


import retrofit2.Retrofit;

/**
 * Created by alessioricco on 02/10/2016.
 */

/**
 * Factory for (all) the APIs called in the app
 */
public class APIFactory {

    public static BitcoinChartsAPI createBitcoinChartsAPI(Retrofit retrofit) {
        return retrofit.create(BitcoinChartsAPI.class);
    }


}

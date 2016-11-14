package it.alessioricco.btc.api;


import retrofit2.Retrofit;


/**
 * Factory for (all) the APIs called in the app
 */
public class APIFactory {

    public static BitcoinChartsAPI createBitcoinChartsAPI(Retrofit retrofit) {
        return retrofit.create(BitcoinChartsAPI.class);
    }

}

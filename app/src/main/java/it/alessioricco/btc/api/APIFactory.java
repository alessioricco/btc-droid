package it.alessioricco.btc.api;


import it.alessioricco.btc.api.interfaces.BitcoinChartsAPI;
import it.alessioricco.btc.api.interfaces.RSSFeedAPI;
import retrofit2.Retrofit;


/**
 * Factory for bitcoinCharts the APIs called in the app
 * todo: we can make them generics
 */
public class APIFactory {

    public static BitcoinChartsAPI createBitcoinChartsAPI(Retrofit retrofit) {
        return retrofit.create(BitcoinChartsAPI.class);
    }

    public static RSSFeedAPI createRSSAdapter(Retrofit retrofit) {
        return retrofit.create(RSSFeedAPI.class);
    }
}

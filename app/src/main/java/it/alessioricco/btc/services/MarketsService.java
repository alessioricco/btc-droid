package it.alessioricco.btc.services;

import java.util.List;

import javax.inject.Inject;

import it.alessioricco.btc.api.APIFactory;
import it.alessioricco.btc.api.interfaces.BitcoinChartsAPI;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.Market;
import rx.Observable;

/**
 * Created by alessioricco on 01/10/2016.
 *
 * it will retrieve the markets data from a service provider (example bitcoincharts.com)
 * even if we'll use api calls like http://api.bitcoincharts.com/v1/markets.json
 * we should make it abstract using an interface to allow us to change the provider when needed
 */

public final class MarketsService {

    private static final String LOG_TAG = "MarketsService";

    @Inject
    RestAdapterFactory restAdapterFactory;

    public MarketsService() {

        ObjectGraphSingleton.getInstance().inject(this);
    }

    /**
     * Returns all bookings for the current user
     *
     * @return user's bookings
     */
    public Observable<List<Market>> getMarkets() {

        final BitcoinChartsAPI api = APIFactory.createBitcoinChartsAPI(restAdapterFactory.getJSONRestAdapter());
        return api.getMarkets();
    }

}

package it.alessioricco.btc.services;

import it.alessioricco.btc.injection.ObjectGraphSingleton;

/**
 * Created by alessioricco on 01/10/2016.
 *
 * it will retrieve the markets data from a service provider (example bitcoincharts.com)
 * even if we'll use api calls like http://api.bitcoincharts.com/v1/markets.json
 * we should make it abstract using an interface to allow us to change the provider when needed
 */

public class MarketsService {

    public MarketsService() {

        ObjectGraphSingleton.getInstance().inject(this);
    }

}

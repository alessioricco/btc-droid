package it.alessioricco.btc.api;

import java.util.List;

import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.models.Markets;
import retrofit2.Call;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by alessioricco on 02/10/2016.
 *
 * retrieve data from http://api.bitcoincharts.com/v1/markets.json
 */

public interface BitcoinChartsAPI {

    @GET("/v1/markets.json")
    Observable<List<Market>> getMarkets();

}

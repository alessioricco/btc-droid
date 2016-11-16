package it.alessioricco.btc.api.interfaces;

import java.util.List;

import it.alessioricco.btc.models.Market;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by alessioricco on 02/10/2016.
 *
 * retrieve data from http://api.bitcoincharts.com/v1/markets.json
 */

public interface BitcoinChartsAPI {

    @GET("/v1/markets.json")
    Observable<List<Market>> getMarkets();

    @GET("/v1/trades.csv")
    Call<String> getHistorySample(@Query("symbol") String symbol, @Query("start") long start, @Query("end") Long end);
}

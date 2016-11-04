package it.alessioricco.btc.api;

import android.support.v4.util.Pair;

import java.util.Date;
import java.util.List;

import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.models.Markets;
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

    //api.bitcoincharts.com/v1/trades.csv?symbol=btceUSD
    @GET("/v1/trades.csv")
    Call<String> getHistory(@Query("symbol") String symbol, @Query("start") long start);

    @GET("/v1/trades.csv")
    Call<String> getHistorySample(@Query("symbol") String symbol, @Query("start") long start, @Query("end") Long end);
}

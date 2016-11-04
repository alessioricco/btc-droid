package it.alessioricco.btc.services;

import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import it.alessioricco.btc.api.APIFactory;
import it.alessioricco.btc.api.BitcoinChartsAPI;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.fragments.HistorySample;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.HistoricalValue;
import it.alessioricco.btc.models.MarketHistory;
import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.utils.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import st.lowlevel.storo.Storo;

//import it.alessioricco.btc.models.MarketHistory;

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

        // Perform the request to the server
        // TODO: the restadapter object should be defined inside the APIFactory class
        final BitcoinChartsAPI api = APIFactory.createBitcoinChartsAPI(restAdapterFactory.getJSONRestAdapter());

        return api.getMarkets();
    }

    /**
     * it create the list of calls to the api
     * each call will represent a query for a given moment of the bitcoin history
     * for the symbol (a simbol is market and currency)
     * the result for those calls must be cached
     *
     * @param symbol
     * @return
     */
    private Observable<HistorySample> getListOfCalls(String symbol) {
        return Observable.from(HistorySample.createSamples(symbol));
    }

    /**
     * given a result as csv, trasform it in an historical value to show on the chart
     * @param resultAsString
     * @param index
     * @return
     */
    private HistoricalValue transformCSVToHistoricalValue(final String resultAsString, final int index) {

        Log.i(LOG_TAG, String.format("received %d chars of result for index %d ", resultAsString.length(), index));

        if (StringUtils.isNullOrEmpty(resultAsString)) {
            return null;
        }

        // extract just the first line from the csv response
        final String line = StringUtils.firstLineOf(resultAsString);
        return HistoricalValue.fromCSVLine(line, index);
    }

    /**
     * retrieve the sampling point for the chart
     *
     * TODO: encapsulate all this code in a separate class
     * @param symbol
     * @return
     * @throws IOException
     */
    public Observable<MarketHistory> getHistorySamples(final String symbol) throws IOException {

        final Boolean cacheEnabled = true;

        final MarketHistory m = new MarketHistory();

        final Func1<HistorySample, Observable<HistoricalValue>> query =
                new Func1<HistorySample, Observable<HistoricalValue>>() {
                    @Override public Observable<HistoricalValue> call(final HistorySample sample) {

                        return Observable.create(new Observable.OnSubscribe<HistoricalValue>(){
                            @Override
                            public void call(final Subscriber<? super HistoricalValue> subscriber) {

                                try {

                                    final String cacheKey = String.format("%s%d", sample.getSymbol(), sample.getIndex());
                                    final long cacheDuration = sample.getCacheDuration();

                                    if (cacheEnabled) {
                                        // check cache
                                        final Boolean expired = Storo.hasExpired(cacheKey).execute();
                                        if (expired != null) {

                                            if (expired) {
                                                Storo.delete(cacheKey);
                                                // we need a new object
                                            } else {
                                                // we retrieve the object
                                                //todo: it could return an observable...
                                                Storo.get(cacheKey, HistoricalValue.class)
                                                        .async(new st.lowlevel.storo.model.Callback<HistoricalValue>() {
                                                            @Override
                                                            public void onResult(HistoricalValue cachedResult) {
                                                                subscriber.onNext(cachedResult);    // Pass on the data to subscriber
                                                                Log.i(LOG_TAG, String.format("%s %d get from cache", sample.getSymbol(), sample.getIndex()));
                                                            }
                                                        });

                                                return;
                                            }
                                        }
                                    }

                                    // value is not cached
                                    sample.getCall().enqueue(new Callback<String>() {

                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {

                                            final String body = response.body();

                                            if (StringUtils.isNullOrEmpty(body)) {
                                                return;
                                            }

                                            final HistoricalValue history = transformCSVToHistoricalValue(body, sample.getIndex());
                                            if (history == null) {
                                                return;
                                            }

                                            Log.i(LOG_TAG, String.format("%s %d get from api", sample.getSymbol(), sample.getIndex()));

                                            if (cacheEnabled) {
                                                Storo.put(cacheKey, history)
                                                        .setExpiry(cacheDuration, TimeUnit.MINUTES)
                                                        .execute();
                                            }

                                            subscriber.onNext(history);
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            //todo add a toast
                                            Log.i(LOG_TAG, String.format("error with index %d: %s", sample.getIndex(), t.getLocalizedMessage()));
                                        }
                                    });

                                } catch(Exception e) {
                                    subscriber.onError(e);
                                }

                            }
                        });

                    }
                };


        // we'll transform a list of HistorySample in a list of HistoricalValue in a MarketHistory object
        return getListOfCalls(symbol)
                .flatMap(query)
                .doOnNext(new Action1<HistoricalValue>() {
                    @Override
                    public void call(HistoricalValue historicalValue) {
                        m.put(historicalValue);
                    }
                })
                .concatMap(new Func1<HistoricalValue, Observable<? extends MarketHistory>>() {
                    @Override
                    public Observable<? extends MarketHistory> call(HistoricalValue historicalValue) {
                        return Observable.create(new Observable.OnSubscribe<MarketHistory>(){

                            @Override
                            public void call(Subscriber<? super MarketHistory> subscriber) {
                                subscriber.onNext(m);
                                subscriber.onCompleted();
                            }
                        });
                    }
                });

    }


}

package it.alessioricco.btc.services;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import it.alessioricco.btc.api.APIFactory;
import it.alessioricco.btc.api.BitcoinChartsAPI;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.HistoricalValue;
import it.alessioricco.btc.models.HistoricalValueSample;
import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.models.MarketHistory;
import it.alessioricco.btc.utils.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import st.lowlevel.storo.Storo;

/**
 * Created by alessioricco on 01/10/2016.
 *
 * it will retrieve the markets data from a service provider (example bitcoincharts.com)
 * even if we'll use api calls like http://api.bitcoincharts.com/v1/markets.json
 * we should make it abstract using an interface to allow us to change the provider when needed
 */

public final class MarketsService {

    @Inject
    RestAdapterFactory restAdapterFactory;
    //Retrofit restAdapter;

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

        final List<HistorySample> calls = new ArrayList<HistorySample>();
        int index = 0;
        for(long startSample: HistoricalValueSample.starts) {
            final long endSample = startSample + HistoricalValueSample.ONE_HOUR;
            final long suggestedCacheDuration = HistoricalValueSample.cacheDurationInMinutes[index];
            calls.add(new HistorySample(index,symbol,startSample, endSample, suggestedCacheDuration));
            index++;
        }

        return Observable.from(calls);
    }


    private HistoricalValue transformCSVToHistoricalValue(final String resultAsString, int index) {
        if (StringUtils.isNullOrEmpty(resultAsString)) {
            return null;
        }

        String[] lines = resultAsString.split("\n");
        for(String line: lines) {
            String[] columns = line.split(",");
            HistoricalValue value = new HistoricalValue();
            value.setDate(new Date(1000 * Long.parseLong(columns[0])));
            value.setValue(Double.parseDouble(columns[1]));
            value.setAmount(Double.parseDouble(columns[2]));
            value.setIndex(index);
            // return the 1st value (alternative is to calculate an average value)
            return value;
        }

        return null;
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

        final MarketHistory m = new MarketHistory();
        m.setSymbol(symbol);

        final Func1<HistorySample, Observable<HistoricalValue>> query =
                new Func1<HistorySample, Observable<HistoricalValue>>() {
                    @Override public Observable<HistoricalValue> call(final HistorySample sample) {

                        return Observable.create(new Observable.OnSubscribe<HistoricalValue>(){
                            @Override
                            public void call(final Subscriber<? super HistoricalValue> subscriber) {

                                try {

                                    final String cacheKey = String.format("%s-%d", sample.symbol, sample.index);
                                    //TODO: must be variable and depending on sample.index
                                    final long cacheDuration = sample.cacheDuration;

                                    // check cache
                                    final Boolean expired = Storo.hasExpired(cacheKey).execute();
                                    if (expired != null) {

                                        if (expired) {
                                            Storo.delete(cacheKey);
                                            // we need a new object
                                        } else {
                                            // we retrieve the object
                                            // todo: make it async
                                            Storo.get(cacheKey, String.class)
                                                    .async(new st.lowlevel.storo.model.Callback<String>() {
                                                        @Override
                                                        public void onResult(String cachedResult) {
                                                            final HistoricalValue history = transformCSVToHistoricalValue(cachedResult, sample.index);
                                                            subscriber.onNext(history);    // Pass on the data to subscriber
                                                            //subscriber.onCompleted();     // Signal about the completion subscriber
                                                            Log.i("charts samples", String.format("%s %d get from cache", sample.symbol, sample.index));
                                                            return;
                                                        }
                                                    });
                                            return;
                                        }
                                    }

                                    // value is not cached
                                    sample.call.enqueue(new Callback<String>() {

                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {

                                            final String body = response.body();

                                            if (StringUtils.isNullOrEmpty(body)) {
                                                return;
                                            }

                                            // results must be cached
                                            Storo.put(cacheKey, body)
                                                    .setExpiry(cacheDuration, TimeUnit.MINUTES)
                                                    .execute();

                                            Log.i("charts samples", String.format("%s %d get from api", sample.symbol, sample.index));

                                            if (sample.symbol != m.getSymbol()) {
                                                // we changed symbol in the meantime
                                                // the cached value is still valid because we cached a real result,
                                                // but we cannot associate it to the current stream
                                                Log.i("charts samples", String.format("%s %d is a glitch", sample.symbol, sample.index));
                                                return;
                                            }

                                            final HistoricalValue history = transformCSVToHistoricalValue(body, sample.index);
                                            subscriber.onNext(history);
                                            //subscriber.onCompleted();
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            //todo add a toast
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
                        m.getHistorySamples().put(historicalValue,historicalValue.getIndex());
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

    /**
     *  drawing the chart require a sampling on different values
     *  (now, 10minutes ago, yesterday, etc...)
     *  each of those sampling point is represented by this class
     */
    private class HistorySample {

        final Call<String> call;
        final String symbol;
        final int index;
        final long start;
        final long end;
        final long cacheDuration;

        HistorySample(final int index, final String symbol, final long start,final long end, final long cacheDuration) {
            long now = (new Date()).getTime();
            this.symbol = symbol;
            this.index = index;

            long startFromNow = (now - start)/1000;
            long endFromNow = (now - end)/1000;

            this.start = startFromNow;
            this.end = endFromNow;

            this.cacheDuration = cacheDuration;

            // the call object will be lazy evaluated
            final BitcoinChartsAPI api = APIFactory.createBitcoinChartsAPI(restAdapterFactory.getRawRestAdapter());
            call = api.getHistorySample(symbol, startFromNow, endFromNow);
        }


    }
}

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
import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.models.MarketHistory;
import it.alessioricco.btc.utils.HistorySamplingDescriptor;
import it.alessioricco.btc.utils.HistorySamplingHelper;
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
        //for(long startSample: HistorySamplingHelper) {
        for(int i=0; i < HistorySamplingHelper.MAX_SAMPLES; i++) {
            final HistorySamplingDescriptor sample = HistorySamplingHelper.getSampleDescriptor(i);
            final long startSample = sample.getSample();
            final long endSample = startSample + sample.getDuration();
            final long suggestedCacheDuration = sample.getCacheDuration();
            calls.add(new HistorySample(index,symbol,startSample, endSample, suggestedCacheDuration));
            index++;
        }

        return Observable.from(calls);
    }


    /**
     * given a result as csv, trasform it in an historical value to show on the chart
     * @param resultAsString
     * @param index
     * @return
     */
    private HistoricalValue transformCSVToHistoricalValue(final String resultAsString, int index) {
        if (StringUtils.isNullOrEmpty(resultAsString)) {
            return null;
        }

        String[] lines = resultAsString.split("\n");
        Log.i("charts samples", String.format("received %d lines of result", lines.length));
        for(String line: lines) {
            final String[] columns = line.split(",");
            HistoricalValue value = new HistoricalValue();
            value.setDate(new Date(1000 * Long.parseLong(columns[0])));
            value.setValue(Double.parseDouble(columns[1]));
            value.setAmount(Double.parseDouble(columns[2]));
            value.setIndex(index);
            //TODO: check if index and date are compatible, because we cannot trust in the api endpoint
            Log.i("charts samples", String.format("value is %s at time %s", columns[1], columns[0]));
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

                                    final String cacheKey = String.format("%s%d", sample.symbol, sample.index);
                                    final long cacheDuration = sample.cacheDuration;

                                    // check cache
                                    final Boolean expired = Storo.hasExpired(cacheKey).execute();
                                    if (expired != null) {

                                        if (expired) {
                                            Storo.delete(cacheKey);
                                            // we need a new object
                                        } else {
                                            // we retrieve the object

                                            Storo.get(cacheKey, HistoricalValue.class)
                                                    .async(new st.lowlevel.storo.model.Callback<HistoricalValue>() {
                                                        @Override
                                                        public void onResult(HistoricalValue cachedResult) {
                                                            //final HistoricalValue history = transformCSVToHistoricalValue(cachedResult, sample.index);
                                                            subscriber.onNext(cachedResult);    // Pass on the data to subscriber
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

                                            final HistoricalValue history = transformCSVToHistoricalValue(body, sample.index);
                                            Log.i("charts samples", String.format("%s %d get from api", sample.symbol, sample.index));

                                            Storo.put(cacheKey, history)
                                                    .setExpiry(cacheDuration, TimeUnit.MINUTES)
                                                    .execute();

                                            if (sample.symbol != m.getSymbol()) {
                                                // we changed symbol in the meantime
                                                // the cached value is still valid because we cached a real result,
                                                // but we cannot associate it to the current stream
                                                Log.i("charts samples", String.format("%s %d is a glitch", sample.symbol, sample.index));
                                                return;
                                            }

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
    private final class HistorySample {

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

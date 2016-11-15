package it.alessioricco.btc.services;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.fragments.HistorySample;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.HistoricalValue;
import it.alessioricco.btc.models.MarketHistory;
import it.alessioricco.btc.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
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

public class HistoryService {

    private static final String TAG = HistoryService.class.getSimpleName();

    final protected @Getter @Setter Boolean cacheEnabled = true;
    @Inject
    RestAdapterFactory restAdapterFactory;

    public HistoryService() {

        ObjectGraphSingleton.getInstance().inject(this);
    }

    /**
     * given a result as csv, trasform it in an historical value to show on the chart
     * @param resultAsString
     * @param index
     * @return
     */
    private HistoricalValue transformCSVToHistoricalValue(final String resultAsString, final int index) {

        Log.i(TAG, String.format("received %d chars of result for index %d ", resultAsString.length(), index));

        if (StringUtils.isNullOrEmpty(resultAsString)) {
            return null;
        }

        // extract just the first line from the csv response
        final String line = StringUtils.firstLineOf(resultAsString);
        return HistoricalValue.fromCSVLine(line, index);
    }

    /**
     * compose the single samples in one structure
     * when all the observer completed their tasks
     * @param symbol
     * @return
     */
    public Observable<MarketHistory> getHistory(final String symbol)  {

        // create the list of samples to be retrieved
        final List<HistorySample> listOfSamples = new ArrayList<>();
        for (int i=0; i<MarketHistory.getMaxSamples(); i++) {
            listOfSamples.add(HistorySample.buildSample(symbol, i));
        }

        // create the market history
        final MarketHistory m = new MarketHistory();

        // create the observable function for retrieving and converting all the samples
        final Observable<HistoricalValue> getMarketHistory = Observable
                .from(listOfSamples)
                // for each sample convert it in a HistoricalValue (Observable)
                .flatMap(new Func1<HistorySample, Observable<HistoricalValue>>() {
                    @Override
                    public Observable<HistoricalValue> call(final HistorySample historySample) {

                        if (historySample == null) {
                            return  null;
                        }

                        return Observable.create(new Observable.OnSubscribe<HistoricalValue>() {
                            @Override
                            public void call(Subscriber<? super HistoricalValue> subscriber) {
                                if (subscriber == null) {
                                    return;
                                }
                                HistoryCall(subscriber, historySample);
                            }
                        });
                    }
                })
                // for each historicalValue taken from the net, it fill the MarketHistoryStructure
                .doOnNext(new Action1<HistoricalValue>() {
                    @Override
                    public void call(HistoricalValue historicalValue) {
                        Log.d(TAG, String.format("received historicaValue index %d", historicalValue.getIndex()));
                        m.put(historicalValue);
                    }
                });

        // send the markethistory object
        return Observable.create(new Observable.OnSubscribe<MarketHistory>(){
            @Override
            public void call(final Subscriber<? super MarketHistory> subscriber) {
                getMarketHistory.doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        subscriber.onNext(m);
                        subscriber.onCompleted();
                    }
                }).subscribe();

            }
        });


    }


    /**
     * call the API endpoint, retrieve a CSV, parse it and then
     * send the value using the subscriber
     * @param subscriber
     * @param sample
     */
    protected void HistoryCall(final Subscriber<? super HistoricalValue> subscriber, final HistorySample sample) {
        final String cacheKey = getCacheKey(sample);
        final long cacheDuration = getCacheDuration(sample);

        if (cacheEnabled) {
            // check cache
            final Boolean expired = Storo.hasExpired(cacheKey).execute();
            if (expired != null) {

                if (!expired) {
                    Storo.get(cacheKey, HistoricalValue.class).async(new st.lowlevel.storo.model.Callback<HistoricalValue>() {
                        @Override
                        public void onResult(HistoricalValue history) {
                            Log.i(TAG, String.format("received cached value for index %d ", sample.getIndex()));
                            subscriber.onNext(history);
                            subscriber.onCompleted();
                        }
                    });
                    return;
                }
                // expired, so we'll delete the key
                Storo.delete(cacheKey);
            }
        }

        // not in cache, we have to download it
        sample.getCall().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                final String body = response.body();
                if (StringUtils.isNullOrEmpty(body)) {
                    subscriber.onCompleted();
                    return;
                }
                final HistoricalValue history = transformCSVToHistoricalValue(body, sample.getIndex());
                if (history == null) {
                    subscriber.onCompleted();
                    return;
                }
                if (cacheEnabled) {
                    Storo.put(cacheKey, history)
                            .setExpiry(cacheDuration, TimeUnit.MINUTES)
                            .execute();
                }

                subscriber.onNext(history);
                subscriber.onCompleted();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG,t.getLocalizedMessage());
            }

        });
    }

    private String getCacheKey(final HistorySample sample) {
        return String.format("%s%d", sample.getSymbol(), sample.getIndex());
    }

    private long getCacheDuration(final HistorySample sample) {
        return sample.getCacheDuration();
    }

}


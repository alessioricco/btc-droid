package it.alessioricco.btc.services;

import android.util.Log;

import java.io.IOException;
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
import st.lowlevel.storo.Storo;

/**
 * Created by alessioricco on 01/10/2016.
 *
 * it will retrieve the markets data from a service provider (example bitcoincharts.com)
 * even if we'll use api calls like http://api.bitcoincharts.com/v1/markets.json
 * we should make it abstract using an interface to allow us to change the provider when needed
 */

public class HistoryService {

    private static final String LOG_TAG = "MarketsService";
    protected @Getter @Setter Boolean cacheEnabled = true;
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

        Log.i(LOG_TAG, String.format("received %d chars of result for index %d ", resultAsString.length(), index));

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
     * @throws IOException
     */
    public Observable<MarketHistory> getHistory(final String symbol) throws IOException {

        final MarketHistory m = new MarketHistory();

        return Observable.create(new Observable.OnSubscribe<MarketHistory>(){

            @Override
            public void call(final Subscriber<? super MarketHistory> subscriber) {
                try {
                    // concat all the calls
                    concatObservableHistorySamples(symbol)
                            // send the final object at the end
                            .doOnCompleted(new Action0() {
                                @Override
                                public void call() {
                                    subscriber.onNext(m);
                                    subscriber.onCompleted();
                                }
                            })
                            .doOnError(new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {

                                }
                            })
                            // for each stream result put it in the right place
                            .forEach(new Action1<HistoricalValue>() {
                                @Override
                                public void call(HistoricalValue historicalValue) {
                                    m.put(historicalValue);
                                }
                            });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private Observable<HistoricalValue> concatObservableHistorySamples(final String symbol) throws IOException {

        final List<Observable<HistoricalValue>> listOfSamples = new ArrayList<Observable<HistoricalValue>>();
        for (int i=0; i<MarketHistory.getMaxSamples(); i++) {
            listOfSamples.add(getHistorySample(symbol,i));
        }

        return Observable.concat(listOfSamples);

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
                Log.d("",t.getLocalizedMessage());
            }

        });
    }

    private final String getCacheKey(final HistorySample sample) {
        return String.format("%s%d", sample.getSymbol(), sample.getIndex());
    }

    private final long getCacheDuration(final HistorySample sample) {
        return sample.getCacheDuration();
    }

    /**
     * retrieve a single sample of market history
     * @param symbol
     * @param index
     * @return
     * @throws IOException
     */
    private Observable<HistoricalValue> getHistorySample(final String symbol, final int index) throws IOException {

        return Observable.create(new Observable.OnSubscribe<HistoricalValue>(){
            @Override
            public void call(final Subscriber<? super HistoricalValue> subscriber) {

                final HistorySample sample = HistorySample.buildSample(symbol, index);

                if (sample == null) {
                    subscriber.onCompleted();
                    return;
                }

                HistoryCall(subscriber,sample);

            }
        });

    }
}


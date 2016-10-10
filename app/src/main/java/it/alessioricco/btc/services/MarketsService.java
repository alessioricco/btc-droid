package it.alessioricco.btc.services;

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
import it.alessioricco.btc.utils.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
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

    private MarketHistory transformCSVToHistoricalValues(final String resultAsString) {
        if (StringUtils.isNullOrEmpty(resultAsString)) {
            return null;
        }
        final List<HistoricalValue> resultAsList = new ArrayList<HistoricalValue>();
        String[] lines = resultAsString.split("\n");
        for(String line: lines) {
            String[] columns = line.split(",");
            HistoricalValue value = new HistoricalValue();
            value.setDate(new Date(1000 * Long.parseLong(columns[0])));
            value.setValue(Double.parseDouble(columns[1]));
            value.setAmount(Double.parseDouble(columns[2]));
            resultAsList.add(value);
        }

        final MarketHistory history = new MarketHistory();
        history.setHistory(resultAsList);

        return history;
    }

    /**
     * retrieve the history of the last hour
     * @param symbol
     * @return
     * @throws IOException
     */
    public Observable<MarketHistory> getHistory(final String symbol) throws IOException {

        Observable o = Observable.create(new Observable.OnSubscribe<MarketHistory>() {
            @Override
            public void call(final Subscriber<? super MarketHistory> subscriber) {
                try {

                    // caching
                    final Boolean expired = Storo.hasExpired(symbol).execute();
                    if (expired != null) {

                        if (expired) {
                            Storo.delete(symbol);
                            // we need a new object
                        } else {
                            // we retrieve the object
                            // todo: make it async
                            Storo.get(symbol, String.class)
                                    .async(new st.lowlevel.storo.model.Callback<String>() {
                                        @Override
                                        public void onResult(String cachedResult) {
                                            final MarketHistory history = transformCSVToHistoricalValues(cachedResult);
                                            subscriber.onNext(history);    // Pass on the data to subscriber
                                            subscriber.onCompleted();     // Signal about the completion subscriber
                                            return;
                                        }
                                    });
                            return;
                        }
                    }

                    // rest call
                    final BitcoinChartsAPI api = APIFactory.createBitcoinChartsAPI(restAdapterFactory.getRawRestAdapter());
                    final long start = ((new Date()).getTime() - 1000*60*60)/1000; //1h

                    final Call<String> call = api.getHistory(symbol, start);
                    call.enqueue(new Callback<String>(){

                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            final String body = response.body();

                            if (StringUtils.isNullOrEmpty(body)) {
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                                return;
                            }

                            Storo.put(symbol, body)
                                    .setExpiry(5, TimeUnit.MINUTES)
                                    .execute();

                            final MarketHistory history = transformCSVToHistoricalValues(body);
                            subscriber.onNext(history);
                            subscriber.onCompleted();

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            //todo add a toast
                        }
                    });

                } catch (Exception e) {
                    subscriber.onError(e);        // Signal about the error to subscriber
                }
            }
        });

        return o;
    }
}

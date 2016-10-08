package it.alessioricco.btc.services;

import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.util.Pair;

import javax.inject.Inject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.alessioricco.btc.api.APIFactory;
import it.alessioricco.btc.api.BitcoinChartsAPI;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.HistoricalValue;
import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.utils.StringUtils;
import lecho.lib.hellocharts.model.PointValue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
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

    private List<HistoricalValue> transformCSVToHistoricalValues(final String resultAsString) {
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
            resultAsList.add(value);
        }
        return resultAsList;
    }

    public Observable<List<HistoricalValue>> getHistory(final String symbol) throws IOException {

        Observable o = Observable.create(new Observable.OnSubscribe<List<HistoricalValue>>() {
            @Override
            public void call(final Subscriber<? super List<HistoricalValue>> subscriber) {
                try {

                    String cachedResult = null;
                    final Boolean expired = Storo.hasExpired(symbol).execute();
                    if (expired != null) {

                        if (expired) {
                            Storo.delete(symbol);
                            // we need a new object
                        } else {
                            // we retrieve the object
                            // todo: make it async
                            cachedResult = Storo.get(symbol, String.class).execute();
                        }

                    }

                    if (cachedResult != null) {
                        final List<HistoricalValue> resultAsList = transformCSVToHistoricalValues(cachedResult);
                        subscriber.onNext(resultAsList);    // Pass on the data to subscriber
                        subscriber.onCompleted();     // Signal about the completion subscriber
                        return;
                    }

                    final BitcoinChartsAPI api = APIFactory.createBitcoinChartsAPI(restAdapterFactory.getRawRestAdapter());
                    final long start = ((new Date()).getTime() - 1000*60*60)/1000; //1h

                    final Call<String> call = api.getHistory(symbol, start);
                    call.enqueue(new Callback<String>(){

                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            String body = response.body();

                            //TODO: make it async
                            Storo.put(symbol, body).setExpiry(5, TimeUnit.MINUTES).execute();

                            if (StringUtils.isNullOrEmpty(body)) {
                                subscriber.onNext(null);
                            } else {
                                final List<HistoricalValue> resultAsList = transformCSVToHistoricalValues(body);
                                subscriber.onNext(resultAsList);    // Pass on the data to subscriber
                            }
                            subscriber.onCompleted();     // Signal about the completion subscriber
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

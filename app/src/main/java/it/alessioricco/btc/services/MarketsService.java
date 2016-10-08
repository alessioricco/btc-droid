package it.alessioricco.btc.services;

import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.util.Pair;

import javax.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

/**
 * Created by alessioricco on 01/10/2016.
 *
 * it will retrieve the markets data from a service provider (example bitcoincharts.com)
 * even if we'll use api calls like http://api.bitcoincharts.com/v1/markets.json
 * we should make it abstract using an interface to allow us to change the provider when needed
 */

public final class MarketsService {

    @Inject
    Retrofit restAdapter;

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
        final BitcoinChartsAPI api = APIFactory.createBitcoinChartsAPI(restAdapter);

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
                    //TODO this must be injected!!!!
                    final RestAdapterFactory factory = new RestAdapterFactory();

                    final BitcoinChartsAPI api = APIFactory.createBitcoinChartsAPI(factory.getRawRestAdapter());

                    final long start = ((new Date()).getTime() - 1000*60*60)/1000; //1h
                    //TODO caching the result is MANDATORY
                    final Call<String> call = api.getHistory("btceUSD", start);
                    call.enqueue(new Callback<String>(){

                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            final List<HistoricalValue> resultAsList = transformCSVToHistoricalValues(response.body());
                            if (resultAsList != null) {
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

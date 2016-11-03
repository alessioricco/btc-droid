package it.alessioricco.btc.fragments;

/**
 * Created by alessioricco on 02/11/2016.
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import it.alessioricco.btc.api.APIFactory;
import it.alessioricco.btc.api.BitcoinChartsAPI;
import it.alessioricco.btc.api.RestAdapterFactory;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import lombok.Getter;
import retrofit2.Call;

/**
 *  drawing the chart require a sampling on different values
 *  (now, 10minutes ago, yesterday, etc...)
 *  each of those sampling point is represented by this class
 */
public final class HistorySample {

    private static final String LOG_TAG = "HistorySample";

    @Getter final Call<String> call;
    @Getter final String symbol;
    @Getter final int index;
    @Getter final long start;
    @Getter final long end;
    @Getter final long cacheDuration;

    @Inject
    RestAdapterFactory restAdapterFactory;

    private HistorySample(final int index, final String symbol, final long start, final long duration, final long cacheDuration) {

        ObjectGraphSingleton.getInstance().inject(this);

        long now = (new Date()).getTime();
        this.symbol = symbol;
        this.index = index;

        long startFromNow = (now - start)/1000L;
        long end = startFromNow + duration/1000L;

        this.start = startFromNow;
        this.end = end;

        this.cacheDuration = cacheDuration;

        // the call object will be lazy evaluated
        final BitcoinChartsAPI api = APIFactory.createBitcoinChartsAPI(restAdapterFactory.getRawRestAdapter());
        Log.i(LOG_TAG, String.format("symbol %s index %d start %d end %d duration %d", symbol, index, startFromNow, end, duration));
        call = api.getHistorySample(symbol, startFromNow, end);
    }

    public static List<HistorySample> createSamples(final String symbol) {
        final List<HistorySample> calls = new ArrayList<HistorySample>();
        // start from 1 to save an api call (we already have the current value)
        for(int i = 1; i < HistorySamplingHelper.MAX_SAMPLES; i++) {
            final HistorySamplingDescriptor sample = HistorySamplingHelper.getSampleDescriptor(i);
            if (sample.getEnabled()) {
                final long startSample = sample.getSample();
                final long durationSample = sample.getDuration();
                final long suggestedCacheDuration = sample.getCacheDuration();
                calls.add(new HistorySample(i, symbol, startSample, durationSample, suggestedCacheDuration));
            }
        }
        return calls;
    }

}
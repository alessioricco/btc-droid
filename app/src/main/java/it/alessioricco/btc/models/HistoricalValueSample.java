package it.alessioricco.btc.models;

import java.io.Serializable;

import it.alessioricco.btc.fragments.HistorySamplingHelper;

/**
 * Created by alessioricco on 28/10/2016.
 *
 */



final public class HistoricalValueSample  implements Serializable {

    final private HistoricalValue[] samples = new HistoricalValue[getMaxSamples()];

    final static public int getMaxSamples() {
        return HistorySamplingHelper.MAX_SAMPLES;
    }

    final public HistoricalValue get(int index) {
        return samples[index];
    }

    final public void put(HistoricalValue historicalValue, int index) {
        samples[index] = historicalValue;
    }
}

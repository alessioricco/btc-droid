package it.alessioricco.btc.models;

import java.io.Serializable;

import it.alessioricco.btc.fragments.HistorySamplingHelper;


/**
 * collection of samples used for the chart visualization
 */
final public class MarketHistory implements Serializable {

    final private HistoricalValue[] samples = new HistoricalValue[getMaxSamples()];

    final static public int getMaxSamples() {
        return HistorySamplingHelper.MAX_SAMPLES;
    }

    final public HistoricalValue get(int index) {
        return samples[index];
    }

    final public void put(HistoricalValue historicalValue) {
        samples[historicalValue.getIndex()] = historicalValue;
    }
}

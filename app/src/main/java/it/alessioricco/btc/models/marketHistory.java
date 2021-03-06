package it.alessioricco.btc.models;

import java.io.Serializable;

import it.alessioricco.btc.fragments.HistorySamplingHelper;
import lombok.Getter;


/**
 * collection of samples used for the chart visualization
 * the chart is a bezier curve built on a set of sample values
 *
 * each of these values is the result of a query
 *
 * todo: implement an iterator interface
 */
final public class MarketHistory implements Serializable {

    final static private @Getter int maxSamples = HistorySamplingHelper.MAX_SAMPLES;
    final private HistoricalValue[] samples = new HistoricalValue[maxSamples];

    /**
     * retrieve a sample from the market history
     * @param index
     * @return
     */
    final public HistoricalValue get(int index) {
        if (index >= maxSamples) return null;
        return samples[index];
    }

    /**
     * add a sample to the market history
     * @param historicalValue
     */
    final public void put(HistoricalValue historicalValue) {
        samples[historicalValue.getIndex()] = historicalValue;
    }

    /**
     * true if the number of samples
     * is good for visualization
     * @return
     */
    final public boolean hasValidData() {
        int n = 0;
        for (HistoricalValue sample: samples) {
            if (sample != null && sample.isValid()) {
                n++;
            }
        }
        return n>1;
    }
}

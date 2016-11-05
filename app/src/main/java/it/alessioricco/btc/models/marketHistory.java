package it.alessioricco.btc.models;

import java.io.Serializable;

import it.alessioricco.btc.fragments.HistorySamplingHelper;
import lombok.Getter;


/**
 * collection of samples used for the chart visualization
 */
final public class MarketHistory implements Serializable {

    final static private @Getter int maxSamples = HistorySamplingHelper.MAX_SAMPLES;
    final private HistoricalValue[] samples = new HistoricalValue[maxSamples];

//    public MarketHistory() {
//        for (int i=0; i< maxSamples; i++) {
//            samples[i].setIndex(-1);
//        }
//    }

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
}

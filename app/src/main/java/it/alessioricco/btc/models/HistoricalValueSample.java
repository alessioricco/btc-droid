package it.alessioricco.btc.models;

import java.io.Serializable;

/**
 * Created by alessioricco on 28/10/2016.
 */

final public class HistoricalValueSample  implements Serializable {

    final static long TO_MINUTES = 60000L;

    final public static long ONE_HOUR = TO_MINUTES*60;
    final static long HALF_DAY = ONE_HOUR*12;
    final static long ONE_DAY = ONE_HOUR*24;
    final static long THREE_DAYS = ONE_DAY*3;
    final static long ONE_WEEK = ONE_DAY*7;



    //list of samplings
    final public static long[] starts = {0, ONE_HOUR, HALF_DAY, ONE_DAY, THREE_DAYS, ONE_WEEK};
    final public static long[] cacheDurationInMinutes = {5, 15, ONE_HOUR/TO_MINUTES, HALF_DAY/TO_MINUTES, ONE_DAY/TO_MINUTES, THREE_DAYS/TO_MINUTES};
    public final static int MAX_SAMPLES= starts.length;

    final private HistoricalValue[] samples = new HistoricalValue[MAX_SAMPLES];

    final public HistoricalValue get(int index) {
        return samples[index];
    }

    final public void put(HistoricalValue historicalValue, int index) {
        samples[index] = historicalValue;
    }
}

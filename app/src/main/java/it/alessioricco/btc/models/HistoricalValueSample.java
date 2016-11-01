package it.alessioricco.btc.models;

import java.io.Serializable;

/**
 * Created by alessioricco on 28/10/2016.
 */

final public class HistoricalValueSample  implements Serializable {


//    public enum SAMPLE_VALUES {
//        NOW, HOUR, DAY, WEEK, MONTH, THREEMONTHS, SIXMONTHS, YEAR, TWOYEARS
//    }

//    final public static long ONE_HOUR = 1000*60*60;
//    final static long ONE_DAY = ONE_HOUR*24;
//    final static long ONE_WEEK = ONE_DAY*7;
//    final static long ONE_MONTH = ONE_DAY*30;
//    final static long THREE_MONTHS = ONE_MONTH*3;
//    final static long SIX_MONTHS = ONE_MONTH*6;
//    final static long ONE_YEAR = ONE_DAY*365;
//    final static long TWO_YEARS = ONE_YEAR*2;
//    final public static long[] starts = {0,ONE_HOUR, ONE_DAY, ONE_WEEK, ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, TWO_YEARS};
//


    final public static long ONE_HOUR = 1000*60*60;
    final static long HALF_DAY = ONE_HOUR*12;
    final static long ONE_DAY = ONE_HOUR*24;
    final static long THREE_DAYS = ONE_DAY*3;
    final static long ONE_WEEK = ONE_DAY*7;
    final static long TWO_WEEKS = ONE_WEEK*2;
//    final static long THREE_WEEKS = ONE_WEEK*3;
//    final static long ONE_MONTH = ONE_DAY*30;

    //TODO: current value could be added with no queries
    final public static long[] starts = {0, ONE_HOUR, HALF_DAY, ONE_DAY, THREE_DAYS, ONE_WEEK};
    public final static int MAX_SAMPLES= starts.length; //9;


//    final private int valueToOrdinal(SAMPLE_VALUES sample) {
//        return sample.ordinal();
//    }
//
//    final private SAMPLE_VALUES ordinalToValue(int ordinal) {
//        return SAMPLE_VALUES.values()[ordinal];
//    }

    final private HistoricalValue[] samples = new HistoricalValue[MAX_SAMPLES];

//    final public HistoricalValue get(SAMPLE_VALUES value) {
//        return samples[valueToOrdinal(value)];
//    }

//    final public void put(HistoricalValue historicalValue, SAMPLE_VALUES value) {
//         samples[valueToOrdinal(value)] = historicalValue;
//    }

    final public HistoricalValue get(int index) {
        return samples[index];
    }

    final public void put(HistoricalValue historicalValue, int index) {
        samples[index] = historicalValue;
    }
}

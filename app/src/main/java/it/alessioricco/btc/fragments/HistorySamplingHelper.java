package it.alessioricco.btc.fragments;

import it.alessioricco.btc.fragments.HistorySamplingDescriptor;

/**
 * define the samples to be visualized on the chart
 */
final public class HistorySamplingHelper  {

    final static long ONE_MINUTE = 60000L;

    final static long TEN_MINUTES = ONE_MINUTE * 10;
    final static long ONE_HOUR = ONE_MINUTE * 60;
    final static long SIX_HOURS = ONE_HOUR * 6;
    final static long HALF_DAY =   ONE_HOUR * 12;
    final static long ONE_DAY =    ONE_HOUR * 24;
    final static long TWO_DAYS =  ONE_DAY * 2;
    final static long THREE_DAYS =  ONE_DAY * 3;
    final static long FIVE_DAYS =  ONE_DAY * 5;
    final static long ONE_WEEK =    ONE_DAY * 7;
    final static long ONE_MONTH =   ONE_DAY * 30;

    final private static HistorySamplingDescriptor[] samples = {
            new HistorySamplingDescriptor("Now",            0,  ONE_MINUTE, 5, true), // not used
            new HistorySamplingDescriptor("10m",  TEN_MINUTES, TEN_MINUTES, 2, true),
            new HistorySamplingDescriptor( "1h",     ONE_HOUR, TEN_MINUTES, 15, true),
            new HistorySamplingDescriptor( "6h",    SIX_HOURS, TEN_MINUTES, ONE_HOUR / ONE_MINUTE, true),
            new HistorySamplingDescriptor("12h",     HALF_DAY, TEN_MINUTES, ONE_HOUR / ONE_MINUTE, true),
            new HistorySamplingDescriptor( "1d",      ONE_DAY, TEN_MINUTES, ONE_HOUR / ONE_MINUTE, true),
            new HistorySamplingDescriptor( "2d",     TWO_DAYS,TEN_MINUTES, SIX_HOURS / ONE_MINUTE, true),
            new HistorySamplingDescriptor( "5d",    FIVE_DAYS, TEN_MINUTES, ONE_DAY / ONE_MINUTE, true),
            new HistorySamplingDescriptor( "1w",     ONE_WEEK, TEN_MINUTES, THREE_DAYS / ONE_MINUTE, false),
            new HistorySamplingDescriptor( "1M",    ONE_MONTH,TEN_MINUTES, ONE_WEEK / ONE_MINUTE, false)
    };

    public final static int MAX_SAMPLES = samples.length;

    /**
     * retrieve a sample descriptor
     * @param index
     * @return
     */
    public static HistorySamplingDescriptor getSampleDescriptor(int index) {
        if (index <0 || index >= MAX_SAMPLES) {
            return null;
        }
        return samples[index];
    }


}
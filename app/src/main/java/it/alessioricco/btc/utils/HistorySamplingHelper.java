package it.alessioricco.btc.utils;

/**
 * Created by alessioricco on 01/11/2016.
 */

final public class HistorySamplingHelper  {

    final static long TO_MINUTES = 60000L;

    final static long ONE_HOUR = TO_MINUTES * 60;
    final static long HALF_DAY = ONE_HOUR * 12;
    final static long ONE_DAY = ONE_HOUR * 24;
    final static long THREE_DAYS = ONE_DAY * 3;
    final static long ONE_WEEK = ONE_DAY * 7;

    final private static HistorySamplingDescriptor[] samples = {
            new HistorySamplingDescriptor("Now",0,ONE_HOUR,5),
            new HistorySamplingDescriptor( "1h",ONE_HOUR,ONE_HOUR,15),
            new HistorySamplingDescriptor("12h",HALF_DAY,ONE_HOUR,ONE_HOUR / TO_MINUTES),
            new HistorySamplingDescriptor( "1d",ONE_DAY,ONE_HOUR,HALF_DAY / TO_MINUTES),
            new HistorySamplingDescriptor( "3d",THREE_DAYS,ONE_HOUR,ONE_DAY / TO_MINUTES),
            new HistorySamplingDescriptor( "1w",ONE_WEEK,ONE_HOUR,THREE_DAYS / TO_MINUTES)
    };

    public final static int MAX_SAMPLES = samples.length;

    public static HistorySamplingDescriptor getSampleDescriptor(int index) {
        return samples[index];
    }


}
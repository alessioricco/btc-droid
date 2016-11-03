package it.alessioricco.btc.models;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 07/10/2016.
 */

public class HistoricalValue implements Serializable {

    private @Getter @Setter int index;
    private @Getter @Setter Date date;
    private @Getter @Setter Double value;
    private @Getter @Setter Double amount;

    static public final HistoricalValue fromCSVLine(final String line, final int index) {
        final String[] columns = line.split(",");
        HistoricalValue value = new HistoricalValue();
        value.setDate(new Date(1000 * Long.parseLong(columns[0])));
        value.setValue(Double.parseDouble(columns[1]));
        value.setAmount(Double.parseDouble(columns[2]));
        value.setIndex(index);
        //TODO: check if index and date are compatible, because we cannot trust in the api endpoint
        //Log.i(LOG_TAG, String.format("value is %s at time %s", columns[1], columns[0]));
        // return the 1st value (alternative is to calculate an average value)
        return value;
    }
}

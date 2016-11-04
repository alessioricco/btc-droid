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

    static public HistoricalValue fromCSVLine(final String line, final int index) {

        try {

            final String[] columns = line.split(",");
            HistoricalValue value = new HistoricalValue();
            value.setDate(new Date(1000 * Long.parseLong(columns[0])));
            value.setValue(Double.parseDouble(columns[1]));
            value.setAmount(Double.parseDouble(columns[2]));
            value.setIndex(index);
            return value;

        } catch(Exception e) {

            return null;

        }
    }
}

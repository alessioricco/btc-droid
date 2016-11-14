package it.alessioricco.btc.models;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


public class HistoricalValue implements Serializable {

    private @Getter @Setter int index = -1;
    private @Getter @Setter Date date = null;
    private @Getter @Setter Double value = null;
    private @Getter @Setter Double amount = null;

    public boolean isValid() {
        return !(index == -1 || date == null || value == null);
    }

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

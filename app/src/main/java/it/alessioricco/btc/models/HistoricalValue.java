package it.alessioricco.btc.models;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 07/10/2016.
 */

public class HistoricalValue implements Serializable {

    private @Getter @Setter Date date;
    private @Getter @Setter Double value;

}

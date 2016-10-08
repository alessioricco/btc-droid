package it.alessioricco.btc.models;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 08/10/2016.
 */

public class MarketHistory  implements Serializable {

    private @Getter @Setter
    List<HistoricalValue> history;


}

package it.alessioricco.btc.models;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 08/10/2016.
 */


public class MarketHistory  implements Serializable {

    //TODO: manage those fields
    private @Getter @Setter boolean isCached = false;
    private @Getter @Setter String symbol;
    private @Getter @Setter String currency;

    private @Getter @Setter HistoricalValueSample historySamples = new HistoricalValueSample();
}

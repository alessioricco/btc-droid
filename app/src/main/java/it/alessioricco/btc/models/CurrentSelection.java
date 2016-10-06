package it.alessioricco.btc.models;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 06/10/2016.
 *
 * it keeps the current selected items and a map
 * of the last selected symbols per each currency
 *
 * TODO: probably we need just two members (currentMarketSymbol,currentMarketCurrency)
 */

public class CurrentSelection {

    private @Getter @Setter String currentMarketSymbol = "";
    private @Getter @Setter String currentMarketCurrency = "";

    private Map<String,String> lastSelectedSymbol = new HashMap<String, String>();

    public String getLastSelectedSymbol(final String currency) {
        return "";
    }

    public void setLastSelectedSymbol(final String currency, final String symbol) {

    }
}

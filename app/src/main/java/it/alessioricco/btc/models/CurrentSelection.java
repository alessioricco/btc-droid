package it.alessioricco.btc.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import it.alessioricco.btc.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 06/10/2016.
 *
 * it keeps the current selected items and a map
 * of the last selected list_of_symbols_main per each currency
 *
 * TODO: probably we need just two members (currentMarketSymbol,currentMarketCurrency)
 */

public class CurrentSelection implements Serializable {

    private String currentMarketSymbol = "";
    private String currentMarketCurrency = "";

    private Map<String,String> lastSelectedSymbol = new HashMap<String, String>();

    public String getCurrentMarketCurrency() {
        if (StringUtils.isNullOrEmpty(currentMarketCurrency)) {
            currentMarketCurrency = "USD";
        }
        return currentMarketCurrency;
    }

    public void setCurrentMarketCurrency(final String currency) {
        currentMarketCurrency = currency;
        currentMarketSymbol = "";
    }

    public void setCurrentMarketSymbol(final String symbol) {
        if (StringUtils.isNullOrEmpty(symbol)) return;

        currentMarketSymbol = symbol;
        lastSelectedSymbol.put(currentMarketCurrency, currentMarketSymbol);
    }

    public String getCurrentMarketSymbol() {
        if (StringUtils.isNullOrEmpty(currentMarketSymbol)) {
            // get the last selected one
            currentMarketSymbol = lastSelectedSymbol.get(getCurrentMarketCurrency());
        }

        return currentMarketSymbol;
    }
}

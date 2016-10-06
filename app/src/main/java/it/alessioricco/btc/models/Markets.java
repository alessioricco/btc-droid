package it.alessioricco.btc.models;

import android.support.v4.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.alessioricco.btc.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 01/10/2016.
 *
 * this model represent the available markets
 * after been filtered and sorted
 * it provides the currencies and symbols structures
 * is an array of Market objects
 */
public class Markets implements Serializable {

    // hashtable of hashtable (currency->(symbol->market))
    final private @Getter @Setter Map<String, Map<String,Market>> currencies = new HashMap<String, Map<String,Market>>();

    /**
     * return the sorted list of available currencies
     * @return a list of string with the currencies id
     */
    public List<String> getCurrencies() {
        final List<String> currencies = new ArrayList(this.currencies.keySet());
        Collections.sort(currencies, new Comparator<String>() {
            public int compare(String left, String right) {
                //TODO: sorting must be done giving priority to the most used currency
                return left.compareTo(right);
            }
        });
        return currencies;
    }

    /**
     * given the currency, retrieve the list of symbols
     * @param currency
     * @return
     */
    public List<String> getSymbols(final String currency) {
        return new ArrayList<String>(currencies.get(currency).keySet());
    }

    /**
     * give currency and market symbol, retrieve the current market
     * @param currency
     * @param symbol
     * @return the given market or null if the market is not found
     */
    public Market getMarket(final String currency, String symbol) {

        if (StringUtils.isNullOrEmpty(currency)) {
            return null;
        }

        if (StringUtils.isNullOrEmpty(symbol)) {
            List<String> symbols = getSymbols(currency);
            if (symbols == null || symbols.size() == 0) return null;
            symbol = symbols.get(0);
        }

        return currencies.get(currency).get(symbol);
    }

    /**
     * filter and sort Markets and other data structures
     * @param markets
     */
    public void setMarkets(List<Market> markets) {

        if (markets == null) {
            return;
        }
        if (markets.size() == 0) {
            return;
        }

        currencies.clear();

        // create the needed data structure (currency and markets)
        //TODO: currencies must be an hashmap of list of symbols

        for (Iterator<Market> iterator = markets.iterator(); iterator.hasNext(); ) {
            final Market m = iterator.next();

            // apply a filter (no need for now)
            if (! m.isValid()) {
                iterator.remove();
                continue;
            }

            final String currency = m.getCurrency();

            //TODO: can we optimize it?
            Map<String,Market> availableMarketsOnGivenCurrency = currencies.get(currency);
            if (availableMarketsOnGivenCurrency == null) {
                availableMarketsOnGivenCurrency = new HashMap<String,Market>();
            }
            final String symbol = m.getSymbol();
            availableMarketsOnGivenCurrency.put(symbol, m);
            currencies.put(currency, availableMarketsOnGivenCurrency);

        }


    }

}


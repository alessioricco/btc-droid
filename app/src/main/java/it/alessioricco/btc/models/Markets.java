package it.alessioricco.btc.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.alessioricco.btc.utils.BitcoinChartsUtils;
import it.alessioricco.btc.utils.StringUtils;
import lombok.Getter;
import rx.Observable;

/**
 * Created by alessioricco on 01/10/2016.
 *
 * this model represent the available markets
 * after been filtered and sorted
 *
 * is based on a map between currency and the map between symbol and market
 */
public class Markets implements Serializable {

    // (currency->(symbol->market))
    final private Map<String, Map<String,Market>> currencies = new HashMap<String, Map<String,Market>>();

    //todo: make them resources
    final private static @Getter String[] privilegedCurrencies = new String[] {"EUR", "GBP", "USD"};
    /**
     * return the sorted list of available currencies
     * @return a list of string with the currencies id
     */
    private List<String> getCurrencies() {
        final List<String> currencies = new ArrayList<>(this.currencies.keySet());

        Collections.sort(currencies, new Comparator<String>() {
            public int compare(String left, String right) {

                for (String currency: privilegedCurrencies) {
                    if (left.equals(currency)) return -100;
                    if (right.equals(currency)) return 100;
                }

                return left.compareTo(right);
            }
        });
        return currencies;
    }

    public Observable<String> getCurrenciesAsObservable() {
        return Observable.from(getCurrencies());
    }

    /**
     * given the currency, retrieve the list of list_of_symbols_main
     * @param currency
     * @return
     */
    private List<String> getSymbols(final String currency) {
        Map<String,Market> symbols = currencies.get(currency);

        if (symbols == null) {
            return null; //TODO it should not happen
        }
        List<String> a = new ArrayList<String>(symbols.keySet());
        Collections.sort(a, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return a;
    }

    public Observable<String> getSymbolsAsObservable(final String currency) {
        return Observable.from(getSymbols(currency));
    }

    /**
     * give currency and market symbol, retrieve the current market
     * @param currency
     * @param symbol
     * @return the given market or null if the market is not found
     */
    public Market getMarket(String currency, String symbol) {

        if (StringUtils.isNullOrEmpty(currency)) {
            return null; //TODO it should not happen
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

        // given the markets list we'll create the currencies data structure
        for (Iterator<Market> iterator = markets.iterator(); iterator.hasNext(); ) {
            final Market m = iterator.next();

            // apply a filter (no need for now)
            if (! m.isValid()) {
                continue;
            }

            final String currency = m.getCurrency();

            //TODO: can we optimize it?
            Map<String,Market> availableMarketsOnGivenCurrency = currencies.get(currency);
            if (availableMarketsOnGivenCurrency == null) {
                availableMarketsOnGivenCurrency = new HashMap<String,Market>();
            }
            final String symbol = BitcoinChartsUtils.normalizeSymbolName(m.getSymbol());
            availableMarketsOnGivenCurrency.put(symbol, m);
            currencies.put(currency, availableMarketsOnGivenCurrency);

        }


    }

}


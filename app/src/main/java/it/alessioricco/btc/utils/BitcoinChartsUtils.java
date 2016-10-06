package it.alessioricco.btc.utils;

/**
 * Created by alessioricco on 06/10/2016.
 */

public class BitcoinChartsUtils {

    static public String normalizeSymbolName(final String symbol) {
        return symbol.substring(0,symbol.length()-3).toUpperCase();
    }

}

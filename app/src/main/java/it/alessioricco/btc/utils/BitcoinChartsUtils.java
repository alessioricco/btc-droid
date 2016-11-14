package it.alessioricco.btc.utils;

public class BitcoinChartsUtils {

    static public String normalizeSymbolName(final String symbol) {
        return symbol.substring(0,symbol.length()-3).toUpperCase();
    }

}

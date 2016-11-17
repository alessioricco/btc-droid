package it.alessioricco.btc.utils;

public class BitcoinChartsUtils {

    /**
     * remove the last three letters from a Symbol name
     * and uppercase the remaining string
     *
     * @param symbol
     * @return
     */
    static public String normalizeSymbolName(final String symbol) {
        if (StringUtils.isNullOrEmpty(symbol)) {
            return "";
        }
        if (symbol.length() <= 3) {
            return "";
        }
        return symbol.substring(0,symbol.length()-3).toUpperCase();
    }

}

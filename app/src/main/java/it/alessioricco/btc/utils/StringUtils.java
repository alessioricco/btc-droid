package it.alessioricco.btc.utils;

/**
 * Created by alessioricco on 02/10/2016.
 */

public class StringUtils {

    /**
     * check for string emptyness or nullity
     * @param string the given string to test
     * @return true if the string is null or if its length is 0
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String formatValue(double value) {
        //todo: fix it
        return String.format("%.2f", value);
    }

    public static String formatPercentValue(double value) {
        //todo: fix it
        return String.format("%+.2f%%", value);
    }

    public static String firstLineOf(final String lines) {
        int eol = lines.indexOf('\n');
        if (eol == -1) return lines;
        return lines.substring(0,eol);
    }

}

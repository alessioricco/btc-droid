package it.alessioricco.btc.utils;

import android.os.Build;
import android.text.Html;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        return String.format(Locale.getDefault(),"%.2f", value);
    }

    public static String formatPercentValue(double value) {
        //todo: fix it
        return String.format(Locale.getDefault(),"%+.2f%%", value);
    }

    public static String firstLineOf(final String lines) {
        if (isNullOrEmpty(lines)) {
            return "";
        }
        int eol = lines.indexOf('\n');
        if (eol == -1) return lines;
        return lines.substring(0,eol);
    }

    public static String removeHtmlTags(final String html) {
        if (isNullOrEmpty(html)) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString(); // for 24 api and more
        } else {
            return Html.fromHtml(html).toString(); // or for older api
        }
    }

    public static String RFC_1123_DATE_TIME = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public static String formatRSSDate(final String pubDate) {
        try {
            final DateFormat formatterInput = new SimpleDateFormat(RFC_1123_DATE_TIME,Locale.getDefault());
            final Date date = formatterInput.parse(pubDate);
            final DateFormat formatterOutput = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm",Locale.getDefault());
            return formatterOutput.format(date);
        } catch (ParseException e) {
            return pubDate;
        } catch (NullPointerException e) {
            return "";
        }

    }
}

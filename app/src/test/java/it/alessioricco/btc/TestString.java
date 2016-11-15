package it.alessioricco.btc;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.alessioricco.btc.utils.StringUtils;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TestString {

    @Test
    public void testLines() throws Exception {

        String test1 = "first line\nsecond line";

        String lines[] = test1.split("\n");

        assertEquals(StringUtils.firstLineOf(test1), lines[0]);
    }

    @Test
    public void testPubDate() throws Exception {
        String pubDate = "Tue, 15 Nov 2016 22:00:06 +0000";

        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        Date date = formatter.parse(pubDate);
        DateFormat formatterOutput = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
        assertTrue(pubDate.startsWith(formatterOutput.format(date)));

    }
}
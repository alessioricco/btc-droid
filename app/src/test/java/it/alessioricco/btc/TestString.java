package it.alessioricco.btc;

import org.junit.Test;

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
}
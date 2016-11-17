package it.alessioricco.btc.util;


import org.junit.Test;

import it.alessioricco.btc.utils.BitcoinChartsUtils;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class TestBitcoinChartsUtil {

    @Test
    public void TestBitcoinChartsUtil() throws Exception {

        assertThat(BitcoinChartsUtils.normalizeSymbolName(null)).isEmpty();
        assertThat(BitcoinChartsUtils.normalizeSymbolName("")).isEmpty();
        assertThat(BitcoinChartsUtils.normalizeSymbolName("1")).isEmpty();
        assertThat(BitcoinChartsUtils.normalizeSymbolName("12")).isEmpty();
        assertThat(BitcoinChartsUtils.normalizeSymbolName("123")).isEmpty();
        assertThat(BitcoinChartsUtils.normalizeSymbolName("1234")).isEqualTo("1");
        assertThat(BitcoinChartsUtils.normalizeSymbolName("alpha")).isEqualTo("AL");
        assertThat(BitcoinChartsUtils.normalizeSymbolName("alphaBTC")).isEqualTo("ALPHA");

    }

}

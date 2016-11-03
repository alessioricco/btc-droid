package it.alessioricco.btc.models;

import org.junit.Test;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class TestMarketHistory {

    /**
     * testing an exception when we access a non valid sample
     */
    @Test
    public void testIndexOutOfBoundsException() {
        MarketHistory h = new MarketHistory();
        assertThat(h.get(MarketHistory.getMaxSamples())).isNull();
    }

}

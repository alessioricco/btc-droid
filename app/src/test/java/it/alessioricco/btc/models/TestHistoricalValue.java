package it.alessioricco.btc.models;

import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class TestHistoricalValue {

    /**
     * testing the historical samples
     * @throws Exception
     */
    @Test
    public void testHistoricalValueSample() throws Exception {
        MarketHistory h = new MarketHistory();

        for (int i = 0; i< MarketHistory.getMaxSamples(); i++) {
            final HistoricalValue hv0 = new HistoricalValue();
            hv0.setIndex(i);
            hv0.setAmount(10d*i);
            hv0.setDate(new Date());
            hv0.setValue(100d*i);

            assertThat(hv0.getIndex()).isEqualTo(i);
            assertThat(hv0.getAmount()/10d).isEqualTo(i);
            assertThat(hv0.getValue()/100d).isEqualTo(i);

            //todo: remove the need for an index as argument or remove the index from the historicalValue
            h.put(hv0);
            assertThat(h.get(hv0.getIndex()).getIndex()).isEqualTo(hv0.getIndex());
        }

    }




}

package it.alessioricco.btc.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.util.List;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.mocks.MockBitcoinCharts;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * TestMarkets
 */

public class TestMarkets {

    @Before
    public void init() throws Exception {

    }

    /**
     * Method executed after any test
     */
    @After
    public void tearDown() {

    }


    /**
     * testing the historical samples
     * @throws Exception
     */
    @Test
    public void testMarkets() throws Exception {

        final List<Market> marketList = MockBitcoinCharts.buildListOfMarketsToTest();
        assertThat(marketList).isNotNull();
        assertThat(marketList.size()).isGreaterThan(0);

        Markets markets = new Markets();
        markets.setMarkets(marketList);

        final List<String> currencies = markets.getCurrencies();
        assertThat(currencies).isNotNull();
        assertThat(currencies.size()).isGreaterThan(0);

        // for each currency we test the symbols
        for(String currency: currencies) {
            final List<String> symbols = markets.getSymbols(currency);
            assertThat(symbols).isNotNull();
            assertThat(symbols.size()).isGreaterThan(0);
        }

        // the market infos are valid
        for(String currency: currencies) {
            for (String symbol: markets.getSymbols(currency)) {
                final Market market = markets.getMarket(currency, symbol);
                assertThat(market).isNotNull();
                assertThat(market.isValid()).isTrue();
            }
        }

    }


}



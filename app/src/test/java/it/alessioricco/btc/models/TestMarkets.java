package it.alessioricco.btc.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import it.alessioricco.btc.mocks.MockBitcoinCharts;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * TestMarkets
 */

public class TestMarkets {

    /**
     * testing the historical samples
     * @throws Exception
     */
    @Test
    public void testMarkets() throws Exception {

        final List<Market> marketList = MockBitcoinCharts.buildListOfMarketsToTest(true);
        assertThat(marketList).isNotNull();
        assertThat(marketList.size()).isGreaterThan(0);

        Markets markets = new Markets();
        markets.setMarkets(marketList);

        final List<String> currencies = markets.getCurrencies();
        assertThat(currencies).isNotNull();
        assertThat(currencies.size()).isGreaterThan(0);

        // the list of privileged currencies is not null
        assertThat(Markets.getPrivilegedCurrencies()).isNotNull();

        // test currency sort
        for (int i=0; i<Markets.getPrivilegedCurrencies().length; i++) {
            assertThat(currencies.get(i)).isEqualTo(Markets.getPrivilegedCurrencies()[i]);
        }
        String lastCurrency = "";
        for (int i=Markets.getPrivilegedCurrencies().length; i<currencies.size(); i++) {
            final String currency = currencies.get(i);
            assertThat(currencies).isNotNull();
            assertThat(currencies).isNotEmpty();
            assertThat(currency.compareTo(lastCurrency)).isGreaterThan(0);
            lastCurrency = currency;
        }

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



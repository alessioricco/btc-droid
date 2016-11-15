package it.alessioricco.btc.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import it.alessioricco.btc.mocks.MockBitcoinCharts;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

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

        Observable<String> currenciesAsObservable = markets.getCurrenciesAsObservable();
        assertThat(currenciesAsObservable).isNotNull();
        final List<String> currenciesAsList = new ArrayList<>();
        currenciesAsObservable.doOnNext(new Action1<String>() {

            @Override
            public void call(String s) {
                currenciesAsList.add(s);
            }
        }).toBlocking().subscribe();
        assertThat(currenciesAsList.size()).isGreaterThan(0);

        // the list of privileged currencies is not null
        assertThat(Markets.getPrivilegedCurrencies()).isNotNull();

        // test currency sort
        for (int i=0; i<Markets.getPrivilegedCurrencies().length; i++) {
            assertThat(currenciesAsList.get(i)).isEqualTo(Markets.getPrivilegedCurrencies()[i]);
        }
        String lastCurrency = "";
        for (int i=Markets.getPrivilegedCurrencies().length; i<currenciesAsList.size(); i++) {
            final String currency = currenciesAsList.get(i);
            assertThat(currenciesAsList).isNotNull();
            assertThat(currenciesAsList).isNotEmpty();
            assertThat(currency.compareTo(lastCurrency)).isGreaterThan(0);
            lastCurrency = currency;
        }

//        // for each currency we test the symbols
//        for(String currency: currenciesAsList) {
//            final List<String> symbols = markets.getSymbols(currency);
//            assertThat(symbols).isNotNull();
//            assertThat(symbols.size()).isGreaterThan(0);
//        }
//
//        // the market infos are valid
//        for(String currency: currenciesAsList) {
//            for (String symbol: markets.getSymbols(currency)) {
//                final Market market = markets.getMarket(currency, symbol);
//                assertThat(market).isNotNull();
//                assertThat(market.isValid()).isTrue();
//            }
//        }

    }

    @Test
    public void testSymbols() throws Exception {

        final List<Market> marketList = MockBitcoinCharts.buildListOfMarketsToTest(true);
        assertThat(marketList).isNotNull();
        assertThat(marketList.size()).isGreaterThan(0);

        final Markets markets = new Markets();
        markets.setMarkets(marketList);

        Observable<String> currenciesAsObservable = markets.getCurrenciesAsObservable();

        final List<String> symbolsAsList = new ArrayList<>();
        currenciesAsObservable
                // for each currency extract the list of symbols
                .map(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String currency) {
                        symbolsAsList.clear();
                        return markets.getSymbolsAsObservable(currency);
                    }
                })
                // for each list of symbols (pay attention! not "for each symbol")
                .doOnNext(new Action1<Observable<String>>() {
                    @Override
                    public void call(Observable<String> symbols) {

                        symbols
                                // for each symbol, add it to the list
                                .doOnNext(new Action1<String>() {
                                    @Override
                                    public void call(String symbol) {
                                        symbolsAsList.add(symbol);
                                    }
                                })
                                // at the end of the iteration assert about the size
                                .doOnCompleted(new Action0() {
                                    @Override
                                    public void call() {
                                        assertThat(symbolsAsList.size()).isGreaterThan(0);
                                    }
                                }).toBlocking().subscribe();
                    }
                })
                .toBlocking().subscribe();


    }

}



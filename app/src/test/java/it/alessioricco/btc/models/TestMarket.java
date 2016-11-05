package it.alessioricco.btc.models;

/**
 * Created by alessioricco on 02/11/2016.
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.util.Date;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.TestEnvironment;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Created by alessioricco on 02/11/2016.
 */

import it.alessioricco.btc.mocks.MockBitcoinCharts;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;

@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestMarket {

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
    public void testMarket() throws Exception {

        Market market = MockBitcoinCharts.buildMarketToTest(true);

        assertThat(market.getHigh()).isEqualTo(9334100d);
        assertThat(market.getBid()).isEqualTo(9285700d);
        assertThat(market.getAsk()).isEqualTo(9298400d);
        assertThat(market.getAvg()).isEqualTo(9278045.917232286432930220455d);
        assertThat(market.getClose()).isEqualTo(9298400d);
        assertThat(market.getLow()).isEqualTo(9202400d);
        assertThat(market.getCurrency()).isEqualTo("IDR");
        assertThat(market.getSymbol()).isEqualTo("btcoidIDR");

        assertThat(market.isValid()).isTrue();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setSymbol("");
        assertThat(market.isValid()).isFalse();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setAsk(null);
        assertThat(market.isValid()).isFalse();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setBid(null);
        assertThat(market.isValid()).isFalse();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setHigh(null);
        assertThat(market.isValid()).isFalse();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setAvg(null);
        assertThat(market.percent()).isNull();
        assertThat(market.delta()).isNull();
        assertThat(market.isValid()).isFalse();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setClose(null);
        assertThat(market.isValid()).isFalse();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setLow(null);
        assertThat(market.isValid()).isFalse();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setCurrency(null);
        assertThat(market.isValid()).isFalse();

        market = MockBitcoinCharts.buildMarketToTest(false);
        assertThat(market.isValid()).isFalse();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setAvg(0d);
        assertThat(market.percent()).isNull();

        market = MockBitcoinCharts.buildMarketToTest(true);
        assertThat(market.getLatest_trade()).isNotNull();
        market.setLatest_trade(null);
        assertThat(market.getLatest_trade()).isNull();

        market = MockBitcoinCharts.buildMarketToTest(true);
        market.setLatest_trade((new Date()).getTime()/1000);

        market.setSymbol("LOCALBTC");
        assertThat(market.isValid()).isFalse();
        market.setSymbol("BITBAY");
        assertThat(market.isValid()).isFalse();
        market.setSymbol("BITCUREX");
        assertThat(market.isValid()).isFalse();
        market.setSymbol("");
        assertThat(market.isValid()).isFalse();
        market.setSymbol(null);
        assertThat(market.isValid()).isFalse();
        market.setSymbol("FOO");
        assertThat(market.isValid()).isTrue();
    }


}


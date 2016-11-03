package it.alessioricco.btc.models;

/**
 * Created by alessioricco on 02/11/2016.
 */

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.util.Date;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.R;
import it.alessioricco.btc.injection.TestObjectGraphInitializer;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Created by alessioricco on 02/11/2016.
 */

import android.content.Context;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.util.Date;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.R;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.injection.TestObjectGraphInitializer;

import static org.junit.Assert.*;
import static org.assertj.core.api.Java6Assertions.*;

@Config(shadows = { ShadowResources.class },
        sdk = 18,
        constants = BuildConfig.class,
        manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class TestMarkets {

    Context context;
    @Before
    public void init() throws Exception {

        // Init the IoC and inject us
        TestObjectGraphInitializer.init();
        TestObjectGraphInitializer.getObjectGraphIstance().inject(this);

        context = RuntimeEnvironment.application;
    }

    /**
     * Method executed after any test
     */
    @After
    public void tearDown() {

        context = null;
        TestObjectGraphInitializer.reset();

    }


    private Market buildMarketToTest() {
        final String jsonMarket = "{\"high\": 9334100.000000000000, \"latest_trade\": 1478109584, \"bid\": 9285700.000000000000, \"volume\": 615.496164440000, \"currency\": \"IDR\", \"currency_volume\": 5710601675.554674000000, \"ask\": 9298400.000000000000, \"close\": 9298400.000000000000, \"avg\": 9278045.917232286432930220455, \"symbol\": \"btcoidIDR\", \"low\": 9202400.000000000000}";

        return new Gson().fromJson(jsonMarket,Market.class);
    }

    /**
     * testing the historical samples
     * @throws Exception
     */
    @Test
    public void testMarket() throws Exception {

        Market market = buildMarketToTest();

        assertThat(market.getHigh()).isEqualTo(9334100d);
        assertThat(market.getBid()).isEqualTo(9285700d);
        assertThat(market.getAsk()).isEqualTo(9298400d);
        assertThat(market.getAvg()).isEqualTo(9278045.917232286432930220455d);
        assertThat(market.getClose()).isEqualTo(9298400d);
        assertThat(market.getLow()).isEqualTo(9202400d);
        assertThat(market.getCurrency()).isEqualTo("IDR");
        assertThat(market.getSymbol()).isEqualTo("btcoidIDR");

        assertThat(market.isValid()).isTrue();

        market = buildMarketToTest();
        market.setSymbol("");
        assertThat(market.isValid()).isFalse();

        market = buildMarketToTest();
        market.setAsk(null);
        assertThat(market.isValid()).isFalse();

        market = buildMarketToTest();
        market.setBid(null);
        assertThat(market.isValid()).isFalse();

        market = buildMarketToTest();
        market.setHigh(null);
        assertThat(market.isValid()).isFalse();

        market = buildMarketToTest();
        market.setAvg(null);
        assertThat(market.percent()).isNull();
        assertThat(market.delta()).isNull();
        assertThat(market.isValid()).isFalse();

        market = buildMarketToTest();
        market.setClose(null);
        assertThat(market.isValid()).isFalse();

        market = buildMarketToTest();
        market.setLow(null);
        assertThat(market.isValid()).isFalse();

        market = buildMarketToTest();
        market.setCurrency(null);
        assertThat(market.isValid()).isFalse();

        market = buildMarketToTest();
        market.setAvg(0d);
        assertThat(market.percent()).isNull();

        market = buildMarketToTest();
        assertThat(market.getLatest_trade()).isNotNull();
        market.setLatest_trade(null);
        assertThat(market.getLatest_trade()).isNull();

        market = buildMarketToTest();
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


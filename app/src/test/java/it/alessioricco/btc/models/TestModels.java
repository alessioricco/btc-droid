package it.alessioricco.btc.models;

/**
 * Created by alessioricco on 02/11/2016.
 */

import android.content.Context;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.util.Date;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.R;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.injection.TestObjectGraphInitializer;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;

import static org.assertj.core.api.Java6Assertions.*;

@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestModels {

    @Before public void init() throws Exception {

        // Init the IoC and inject us
        TestObjectGraphInitializer.init();
        TestObjectGraphInitializer.getObjectGraphIstance().inject(this);

    }

    /**
     * Method executed after any test
     */
    @After public void tearDown() {

        TestObjectGraphInitializer.reset();

    }

    /**
     * testing the historical samples
     * @throws Exception
     */
    @Test
    public void testHistoricalValueSample() throws Exception {
        MarketHistory h = new MarketHistory();

        for (int i=0; i< h.getMaxSamples(); i++) {
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

    /**
     * testing an exception when we access a non valid sample
     */
    @Test(expected=IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsException() {
        MarketHistory h = new MarketHistory();
        h.get(MarketHistory.getMaxSamples());
    }

    @Test
    public void testCurrentSelection() throws Exception {
        CurrentSelection currentSelection = new CurrentSelection();

        assertThat(currentSelection.getCurrentMarketCurrency()).isNotEmpty();
        assertThat(currentSelection.getCurrentMarketCurrency()).isNotNull();

        Context context = RuntimeEnvironment.application;
        // no currency selected
        final String default_currency = context.getString(R.string.currency_usd);
        assertThat(currentSelection.getCurrentMarketCurrency())
                .isEqualToIgnoringCase(default_currency);

        // currency: eur
        final String EUR = "EUR";
        currentSelection.setCurrentMarketCurrency(EUR);
        assertThat(currentSelection.getCurrentMarketCurrency()).isEqualTo(EUR);

        // select one market
        final String BTCEUR = "BTCEUR";
        currentSelection.setCurrentMarketSymbol(BTCEUR);
        assertThat(currentSelection.getCurrentMarketSymbol()).isEqualTo(BTCEUR);

        // select a different market
        final String BTCEEUR = "BTCEEUR";
        currentSelection.setCurrentMarketSymbol(BTCEEUR);
        assertThat(currentSelection.getCurrentMarketSymbol()).isEqualTo(BTCEEUR);

        //  no currency selected again
        currentSelection.setCurrentMarketCurrency("");
        assertThat(currentSelection.getCurrentMarketCurrency()).isEqualTo(default_currency);

        // select again eur and testing the market
        currentSelection.setCurrentMarketCurrency(EUR);
        assertThat(currentSelection.getCurrentMarketCurrency()).isEqualTo(EUR);
        assertThat(currentSelection.getCurrentMarketSymbol()).isEqualTo(BTCEEUR);

    }

}

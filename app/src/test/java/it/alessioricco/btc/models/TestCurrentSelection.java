package it.alessioricco.btc.models;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.R;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.injection.TestObjectGraphInitializer;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestCurrentSelection {

    @Before
    public void init() throws Exception {

        // Init the IoC and inject us
        TestObjectGraphInitializer.init();
        TestObjectGraphInitializer.getObjectGraphIstance().inject(this);

    }

    /**
     * Method executed after any test
     */
    @After
    public void tearDown() {

        TestObjectGraphInitializer.reset();

    }

    @Test
    public void testCurrentSelection() throws Exception {
        CurrentSelection currentSelection = new CurrentSelection();

        assertThat(currentSelection.getCurrentMarketCurrency()).isNotNull();
        assertThat(currentSelection.getCurrentMarketCurrency()).isNotEmpty();

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

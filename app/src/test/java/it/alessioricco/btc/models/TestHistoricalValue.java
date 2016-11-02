package it.alessioricco.btc.models;

/**
 * Created by alessioricco on 02/11/2016.
 */

import org.junit.Assert;
import org.junit.Test;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.util.Date;

import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.injection.TestObjectGraphInitializer;

import static org.junit.Assert.*;
import static org.assertj.core.api.Java6Assertions.*;

@Config(shadows = { ShadowResources.class }) @RunWith(RobolectricTestRunner.class)
public class TestHistoricalValue {

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

    @Test
    public void testHistoricalValueSample() throws Exception {
        HistoricalValueSample h = new HistoricalValueSample();

        for (int i=0; i< h.getMaxSamples(); i++) {
            final HistoricalValue hv0 = new HistoricalValue();
            hv0.setIndex(i);
            hv0.setAmount(10d*i);
            hv0.setDate(new Date());
            hv0.setValue(100d*i);

            assertThat(hv0.getIndex()).isEqualTo(i);
            assertThat(hv0.getAmount()/10d).isEqualTo(i);
            assertThat(hv0.getValue()/100d).isEqualTo(i);

        }

    }

}

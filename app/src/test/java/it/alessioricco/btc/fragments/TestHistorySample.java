package it.alessioricco.btc.fragments;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestHistorySample {

    @Before
    public void init() throws Exception {

    }

    /**
     * Method executed after any test
     */
    @After
    public void tearDown() {

    }
    
    @Test
    public void TestSample() throws Exception {
        final HistorySample sample = HistorySample.buildSample("testSymbol",1);
        assertThat(sample).isNotNull();
        assertThat(sample.getIndex()).isEqualTo(1);

        final HistorySample sample2 = HistorySample.buildSample("testSymbol",-1);
        assertThat(sample2).isNull();

        final HistorySample sample3 = HistorySample.buildSample("testSymbol",HistorySamplingHelper.MAX_SAMPLES);
        assertThat(sample3).isNull();

        //counting disabled samples
        int disabled = 0;
        for (int i=0; i < HistorySamplingHelper.MAX_SAMPLES; i++) {
            final HistorySamplingDescriptor sampleDescriptor = HistorySamplingHelper.getSampleDescriptor(i);
            if (sampleDescriptor == null) {
                continue;
            }
            if (!sampleDescriptor.getEnabled()) {
                disabled++;
            };
        }

    }
}



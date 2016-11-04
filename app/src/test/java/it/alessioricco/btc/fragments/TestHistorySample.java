package it.alessioricco.btc.fragments;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.util.List;

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
        final List<HistorySample> samples = HistorySample.createSamples("testSymbol");
        assertThat(samples).isNotNull();
        assertThat(samples).isNotEmpty();

        //counting disabled samples
        int disabled = 0;
        for (int i=0; i < HistorySamplingHelper.MAX_SAMPLES; i++) {
            final HistorySamplingDescriptor sample = HistorySamplingHelper.getSampleDescriptor(i);
            if (sample.getEnabled() == false) disabled++;
        }

        // the samples are the MAX_SAMPLES-the disbled one
        assertThat(samples.size()).isEqualTo(HistorySamplingHelper.MAX_SAMPLES-1-disabled);
    }
}



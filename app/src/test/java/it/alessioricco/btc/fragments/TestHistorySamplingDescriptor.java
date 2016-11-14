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
public class TestHistorySamplingDescriptor {

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
    public void TestConstructor() throws Exception {

        final String label = "label";
        HistorySamplingDescriptor descriptor = new HistorySamplingDescriptor(label, 1L, 2L, 3L, true);
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getLabel()).isEqualTo(label);
        assertThat(descriptor.getSample()).isEqualTo(1L);
        assertThat(descriptor.getDuration()).isEqualTo(2L);
        assertThat(descriptor.getCacheDuration()).isEqualTo(3L);
        assertThat(descriptor.getEnabled()).isTrue();

    }

    /**
     * all the samples should be not null and they should contain values
     * each sample should be sorted
     * @throws Exception
     */
    @Test
    public void TestHelper() throws Exception {

        long lastSample = Long.MIN_VALUE;
        Boolean lastEnabled = true;

        for (int i=0; i < HistorySamplingHelper.MAX_SAMPLES; i++) {
            final HistorySamplingDescriptor sample = HistorySamplingHelper.getSampleDescriptor(i);
            assertThat(sample).isNotNull();
            assertThat(sample.getLabel()).isNotEmpty();
            assertThat(sample.getLabel()).isNotNull();
            assertThat(sample.getSample()).isGreaterThanOrEqualTo(lastSample);
            assertThat(sample.getCacheDuration()).isGreaterThan(0);
            assertThat(sample.getDuration()).isGreaterThan(0);

            // if the value is false, all the following should be false
            if (!lastEnabled) {
                assertThat(sample.getEnabled()).isFalse();
            }

            lastSample = sample.getSample();
            lastEnabled = sample.getEnabled();
        }
        final HistorySamplingDescriptor sample = HistorySamplingHelper.getSampleDescriptor(HistorySamplingHelper.MAX_SAMPLES);
        assertThat(sample).isNull();
    }


}



package it.alessioricco.btc.fragments;


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
import java.util.List;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.R;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.injection.TestObjectGraphInitializer;
import it.alessioricco.btc.models.HistoricalValue;
import it.alessioricco.btc.models.HistoricalValueSample;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;

import static org.junit.Assert.*;
import static org.assertj.core.api.Java6Assertions.*;

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
            if (lastEnabled == false) {
                assertThat(sample.getEnabled()).isFalse();
            }

            lastSample = sample.getSample();
            lastEnabled = sample.getEnabled();
        }
        final HistorySamplingDescriptor sample = HistorySamplingHelper.getSampleDescriptor(HistorySamplingHelper.MAX_SAMPLES);
        assertThat(sample).isNull();
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



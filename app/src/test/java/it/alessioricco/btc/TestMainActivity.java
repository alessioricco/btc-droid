package it.alessioricco.btc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.util.ActivityController;
import org.robolectric.shadows.ShadowActivity;

import javax.inject.Inject;

import it.alessioricco.btc.injection.TestObjectGraphSingleton;
import it.alessioricco.btc.mocks.MockAppWebServer;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;
import okhttp3.mockwebserver.MockResponse;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by alessioricco on 05/11/2016.
 */

@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestMainActivity {

    @Inject
    MockAppWebServer mockWebServer;

    @Before
    public void init() throws Exception {

        // Init the IoC and inject us
        TestObjectGraphSingleton.init();
        TestObjectGraphSingleton.getInstance().inject(this);

    }

    /**
     * Method executed after any test
     */
    @After
    public void tearDown() {

        TestObjectGraphSingleton.reset();

    }

    /**
     * test what happens when the activity will access the endpoints
     * @throws Exception
     */
    @Test
    public void testDefaultUIResponse200() throws Exception {
        // Create the activity
        ActivityController<MainActivity> activityController =
                Robolectric.buildActivity(MainActivity.class)
                        .create()
                        .start()
                        .resume()
                        .visible();
        MainActivity activity = activityController.get();
        assertThat(activity).isNotNull();
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        assertThat(shadowActivity).isNotNull();
        assertThat(shadowActivity.isFinishing()).isFalse();

        assertThat(activity.currentValue.getText()).isNotNull();
        assertThat(activity.currentValue.getText()).isNotEmpty();

    }

    /**
     * test what happens when the activity is not able to access the endpoint
     * @throws Exception
     */
    @Test
    public void testDefaultUIResponse500() throws Exception {

        mockWebServer.setMockResponse(new MockResponse().setResponseCode(500));

        // Create the activity
        ActivityController<MainActivity> activityController =
                Robolectric.buildActivity(MainActivity.class)
                        .create()
                        .start()
                        .resume()
                        .visible();
        MainActivity activity = activityController.get();
        assertThat(activity).isNotNull();
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        assertThat(shadowActivity).isNotNull();
        assertThat(shadowActivity.isFinishing()).isFalse();

        assertThat(activity.currentValue.getText()).isNotNull();
        assertThat(activity.currentValue.getText()).isEmpty();

        final String emptyNumber = activity.getApplicationContext().getString(R.string.empty_numeric_value);
        assertThat(activity.askValue.getText().toString()).isEqualTo(emptyNumber);

    }
}
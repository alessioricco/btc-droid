package it.alessioricco.btc.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.injection.TestObjectGraphSingleton;
import it.alessioricco.btc.utils.NetworkStatus;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestNetworkStatus {

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

    @Test
    public void testNoNetwork() throws Exception {

        Context context = mock(Context.class);
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);

        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(null);

        assertThat(NetworkStatus.isInternetConnected(context)).isEqualTo(NetworkStatus.NOCONNECTION);
    }

    @Test
    public void testWiFiNetwork() throws Exception {

        Context context = mock(Context.class);
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        NetworkInfo networkInfo = mock(NetworkInfo.class);

        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);

        assertThat(NetworkStatus.isInternetConnected(context)).isEqualTo(NetworkStatus.WIFI);
    }

    @Test
    public void testMobileNetwork() throws Exception {

        Context context = mock(Context.class);
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        NetworkInfo networkInfo = mock(NetworkInfo.class);

        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);

        assertThat(NetworkStatus.isInternetConnected(context)).isEqualTo(NetworkStatus.MOBILE);
    }

    @Test
    public void testOtherNetwork() throws Exception {

        Context context = mock(Context.class);
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        NetworkInfo networkInfo = mock(NetworkInfo.class);

        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_DUMMY);

        assertThat(NetworkStatus.isInternetConnected(context)).isEqualTo(NetworkStatus.OTHERNETWORK);
    }

}

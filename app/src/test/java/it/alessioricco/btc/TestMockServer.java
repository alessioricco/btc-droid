package it.alessioricco.btc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import it.alessioricco.btc.injection.TestObjectGraphSingleton;
import it.alessioricco.btc.mocks.MockBitcoinCharts;
import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.services.MarketsService;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import rx.Observable;
import rx.Subscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;

//http://fedepaol.github.io/blog/2015/09/13/testing-rxjava-observables-subscriptions/
//https://riggaroo.co.za/retrofit-2-mocking-http-responses/
@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestMockServer {

    @Inject
    MockWebServer mockWebServer;
    @Inject
    MarketsService marketService;

    @Before
    public void init() throws Exception {

        TestObjectGraphSingleton.init();
        TestObjectGraphSingleton.getInstance().inject(this);
        mockWebServer.start();
    }

    /**
     * Method executed after any test
     */
    @After
    public void tearDown() {
        try {
            mockWebServer.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void testGetMarketApi() throws Exception {

        final String response = MockBitcoinCharts.getRawResponse();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(response));

        final Observable<List<Market>> market = marketService.getMarkets();

        market.toBlocking()
                .subscribe(new Subscriber<List<Market>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        assertThat(e).isNotNull();
                    }

                    @Override
                    public void onNext(List<Market> markets) {
                        assertThat(markets).isNotNull();
                        assertThat(markets.size()).isGreaterThan(0);
                    }
                });

    }
}

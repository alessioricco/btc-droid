package it.alessioricco.btc.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import java.util.List;

import javax.inject.Inject;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.injection.TestObjectGraphSingleton;
import it.alessioricco.btc.mocks.MockAppWebServer;
import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;
import okhttp3.mockwebserver.MockResponse;
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
public class TestMarketService {

    @Inject
    MockAppWebServer mockWebServer;
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
        mockWebServer.shutdown();
    }

    /**
     * server return a 500
     * we expect an error
     * @throws Exception
     */
    @Test
    public void testGetMarketApi500() throws Exception {

        final Observable<List<Market>> market = marketService.getMarkets();

        mockWebServer.setMockResponse(new MockResponse().setResponseCode(500));

        market.toBlocking()
                .subscribe(new Subscriber<List<Market>>() {

                    @Override
                    public void onCompleted() {
                        assert(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        assertThat(e).isNotNull();
                    }

                    @Override
                    public void onNext(List<Market> markets) {
                        assert(false);
                    }
                });
    }



    /**
     * test server returning empty string
     * we expect an error
     * @throws Exception
     */
    @Test
    public void testGetMarketApiTrash() throws Exception {

        final Observable<List<Market>> market = marketService.getMarkets();

        mockWebServer.setMockResponse(new MockResponse().setResponseCode(200).setBody(""));

        market.toBlocking()
                .subscribe(new Subscriber<List<Market>>() {

                    @Override
                    public void onCompleted() {
                        assert(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        assert(true);
                    }

                    @Override
                    public void onNext(List<Market> markets) {
                        assert(false);
                    }
                });
    }


    /**
     * server return a 200, we expect a list of json values
     * @throws Exception
     */
    @Test
    public void testGetMarketApi200() throws Exception {

        final Observable<List<Market>> market = marketService.getMarkets();

        market.toBlocking()
                .subscribe(new Subscriber<List<Market>>() {

                    @Override
                    public void onCompleted() {
                        assert(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        assert(false);
                    }

                    @Override
                    public void onNext(List<Market> markets) {
                        assertThat(markets).isNotNull();
                        assertThat(markets.size()).isGreaterThan(0);
                    }
                });
    }


}

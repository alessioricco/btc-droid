package it.alessioricco.btc.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import javax.inject.Inject;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.injection.TestObjectGraphSingleton;
import it.alessioricco.btc.mocks.MockAppWebServer;
import it.alessioricco.btc.models.HistoricalValue;
import it.alessioricco.btc.models.MarketHistory;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;
import rx.Observable;
import rx.Subscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestHistoryService {

    @Inject
    MockAppWebServer mockWebServer;
    @Inject
    MarketsService marketService;
    @Inject
    HistoryService historyService;

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
     * server return a 200, we expect a list of json values
     * @throws Exception
     */
    @Test
    public void testGetHistorySample200() throws Exception {

        final Observable<MarketHistory> marketHistory = historyService.getHistory("");

        marketHistory
                .toBlocking()
                .subscribe(new Subscriber<MarketHistory>() {

                    @Override
                    public void onCompleted() {
                        assert(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        assert(false);
                    }

                    @Override
                    public void onNext(MarketHistory marketHistory) {
                        assertThat(marketHistory).isNotNull();
                        int numOfValidResults = 0;
                        for (int i=0; i < MarketHistory.getMaxSamples(); i++) {
                            HistoricalValue value = marketHistory.get(i);
                            //assertThat(value).isNotNull();
                            if (value != null) {
                                assertThat(value.isValid()).isTrue();
                                numOfValidResults++;
                            }
                        }
                        assertThat(numOfValidResults).isGreaterThan(0);
                    }
                });
    }

}

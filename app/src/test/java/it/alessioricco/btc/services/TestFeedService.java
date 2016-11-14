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
import it.alessioricco.btc.models.feed.RSS;
import it.alessioricco.btc.util.CustomRobolectricTestRunner;
import rx.Observable;
import rx.Subscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;


@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestFeedService {

    //@Inject
    //MockAppWebServer mockWebServer;
    @Inject
    FeedService feedService;

    @Before
    public void init() throws Exception {

        TestObjectGraphSingleton.init();
        TestObjectGraphSingleton.getInstance().inject(this);
        //mockWebServer.start();
    }

    /**
     * Method executed after any test
     */
    @After
    public void tearDown() {
        //mockWebServer.shutdown();
    }

    /**
     * server return a 200, we expect a real feed
     * we have few tests, so it's ok but oin real life we should use a mock answer
     * @throws Exception
     */
    @Test
    public void testGetFeed200() throws Exception {

        final Observable<RSS> feed = feedService.getFeed("http://feeds.feedburner.com/CoinDesk");

        feed.toBlocking()
                .subscribe(new Subscriber<RSS>() {

                    @Override
                    public void onCompleted() {
                        assert(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        assert(false);
                    }

                    @Override
                    public void onNext(RSS markets) {
                        assertThat(markets).isNotNull();
                        assertThat(markets.getChannel()).isNotNull();
                        assertThat(markets.getChannel().toString()).isNotNull();
                        assertThat(markets.getChannel().toString()).isNotEmpty();
                    }
                });
    }
}

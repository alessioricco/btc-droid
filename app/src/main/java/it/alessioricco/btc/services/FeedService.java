package it.alessioricco.btc.services;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import it.alessioricco.btc.api.APIFactory;
import it.alessioricco.btc.api.RssAdapterFactory;
import it.alessioricco.btc.api.interfaces.RSSFeedAPI;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.feed.RSS;
import rx.Observable;

public final class FeedService {

    private static final String TAG = FeedService.class.getSimpleName();

    @Inject
    RssAdapterFactory rssAdapterFactory;

    public FeedService() {

        ObjectGraphSingleton.getInstance().inject(this);
    }

    /**
     * Returns all bookings for the current user
     *
     * @return user's bookings
     */
    public Observable<RSS> getFeed(final String endPointUrl) {

        String baseUrl;
        try
        {
            final URL url = new URL(endPointUrl);
            baseUrl = String.format("%s://%s/",url.getProtocol(),url.getHost());
        }
        catch (MalformedURLException e)
        {
            return Observable.error(e);
        }

        final RSSFeedAPI api = APIFactory.createRSSAdapter(rssAdapterFactory.getRssRestAdapter(baseUrl));
        return api.getFeed(endPointUrl);
    }

}
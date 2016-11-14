package it.alessioricco.btc.api.interfaces;


import java.util.List;

import it.alessioricco.btc.models.Market;
import retrofit2.http.GET;
import rx.Observable;

public interface RSSFeedAPI {

    //http://feeds.feedburner.com/CoinDesk

    @GET
    Observable<RSS> getFeed(final String url);
}

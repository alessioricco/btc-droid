package it.alessioricco.btc.api.interfaces;


import it.alessioricco.btc.models.feed.RSS;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface RSSFeedAPI {

    @GET
    Observable<RSS> getFeed(@Url final String url);
}

package it.alessioricco.btc.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.alessioricco.btc.R;
import it.alessioricco.btc.adapters.RSSListAdapter;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.feed.Channel;
import it.alessioricco.btc.models.feed.RSS;
import it.alessioricco.btc.services.FeedService;
import it.alessioricco.btc.utils.Environment;
import it.alessioricco.btc.utils.StringUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FeedRSSActivity extends AppCompatActivity {

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;

    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    FeedService feedService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_rss);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //todo: the fab is not visible, maybe delete it
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // customization
        initialize();
    }

    private void initialize() {
        //begin of the custom code
        ObjectGraphSingleton.getInstance().inject(this);
        ButterKnife.inject(this);

        // initialization
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // pull down
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                fetchFeed();
            }
        });
    }

    private void fetchFeed() {
        compositeSubscription.add(asyncUpdateFeed());
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchFeed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeSubscription.unsubscribe();
    }

    private void errorMessage() {

        Snackbar.make(this.recyclerView, R.string.error_retrieving_data, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        fetchFeed();
                    }
                });
    }

    private void startProgress() {

        if (swipeRefreshLayout.isRefreshing()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * remove the progress dialog
     * <p>
     * it dismiss the the dialog to prevent a leak
     * http://stackoverflow.com/questions/6614692/progressdialog-how-to-prevent-leaked-window
     */
    private void endProgress() {

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        progressBar.setVisibility(View.GONE);
    }

    /**
     * read some feeds merging the news together
     * @return
     */
    private Subscription asyncUpdateFeed() {

        final List<Channel.FeedItem> feedItemList = new ArrayList<>();

        final Observable<RSS> feed1 = this.feedService.getFeed(Environment.newsFeedCoinDeskUrl);
        final Observable<RSS> feed2 = this.feedService.getFeed(Environment.newsFeedBitcoinUrl);
        final Observable<RSS> feeds = Observable.merge(feed1,feed2);

        return feeds
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        feedItemList.clear();
                        startProgress();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends RSS>>() {
                    @Override
                    public Observable<? extends RSS> call(Throwable throwable) {
                        return null;
                    }
                })
                .doOnNext(new Action1<RSS>() {
                    @Override
                    public void call(RSS rss) {

                        if (rss == null || rss.getChannel() == null) {
                            return;
                        }

                        for(Channel.FeedItem item: rss.getChannel().feedItemList){
                            item.setSource(rss.getChannel().getTitle());
                            item.setDescription(StringUtils.removeHtmlTags(item.getDescription()));
                        }
                        feedItemList.addAll(rss.getChannel().feedItemList);

                        // this could be slow, due the getDate() function
                        Collections.sort(feedItemList, new Comparator<Channel.FeedItem>() {
                            @Override
                            public int compare(Channel.FeedItem t1, Channel.FeedItem t2) {
                                return t2.getDate().compareTo(t1.getDate());
                            }
                        });
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        endProgress();
                        final RSSListAdapter adapter = new RSSListAdapter(getApplicationContext(),feedItemList);
                        recyclerView.setAdapter(adapter);
                    }
                }).subscribe();

    }

}

package it.alessioricco.btc.activities;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.alessioricco.btc.R;
import it.alessioricco.btc.adapters.RSSListAdapter;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.feed.RSS;
import it.alessioricco.btc.services.FeedService;
import it.alessioricco.btc.utils.Environment;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class NewsActivity extends AppCompatActivity {

    protected CompositeSubscription compositeSubscription = new CompositeSubscription();

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
        setContentView(R.layout.rss_list);

        //begin of the custom code
        ObjectGraphSingleton.getInstance().inject(this);
        ButterKnife.inject(this);

        // initialization
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        Snackbar.make(this.recyclerView,R.string.error_retrieving_data, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        fetchFeed();
                    }
                });
    }

    void startProgress() {

        if (swipeRefreshLayout.isRefreshing()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * remove the progress dialog
     *
     * it dismiss the the dialog to prevent a leak
     * http://stackoverflow.com/questions/6614692/progressdialog-how-to-prevent-leaked-window
     */
    void endProgress() {

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        progressBar.setVisibility(View.GONE);
    }

    private Subscription asyncUpdateFeed() {

        startProgress();

        final Observable<RSS> observable = this.feedService.getFeed(Environment.newsFeedUrl);
        return observable
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .subscribe(new Subscriber<RSS>() {
                    @Override
                    public void onCompleted() {
                        endProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        endProgress();
                        errorMessage();

                    }

                    @Override
                    public void onNext(RSS rss) {

                        if (rss == null) {
                            return;
                        }
                        final RSSListAdapter adapter = new RSSListAdapter(getBaseContext(), rss);
                        recyclerView.setAdapter(adapter);

                    }

                });



    }

}

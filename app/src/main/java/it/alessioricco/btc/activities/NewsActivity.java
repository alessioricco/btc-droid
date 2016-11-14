package it.alessioricco.btc.activities;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.alessioricco.btc.R;
import it.alessioricco.btc.adapters.RSSListAdapter;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.MarketHistory;
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

    //todo: inject
    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    FeedService feedService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rss_list);

        //begin of the custom code
        ObjectGraphSingleton.getInstance().inject(this);
        ButterKnife.inject(this);

        //
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onResume() {
        super.onResume();

        compositeSubscription.add(asyncUpdateFeed());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeSubscription.unsubscribe();
    }

    private Subscription asyncUpdateFeed() {

        progressBar.setVisibility(View.VISIBLE);

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
                        progressBar.setVisibility(View.GONE);
                        //chartFragmentContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
//                            if (e instanceof HttpException) {
//                                HttpException response = (HttpException) e;
//                                int code = response.code();
//                                //TODO: add a toast
//                            }
                        //TODO: what happens to the UI?
                        progressBar.setVisibility(View.GONE);
                        //chartFragmentContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(RSS rss) {

                        //TODO: double check on the currency/action to avoid wasting time on old selections
                        //drawChart(currentMarket, history);

                        final RSSListAdapter adapter = new RSSListAdapter(getBaseContext(), rss);
                        recyclerView.setAdapter(adapter);

                    }

                });



    }

}

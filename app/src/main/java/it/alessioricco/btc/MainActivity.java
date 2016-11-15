package it.alessioricco.btc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ocpsoft.pretty.time.PrettyTime;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.alessioricco.btc.activities.NewsActivity;
import it.alessioricco.btc.fragments.Chart;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.CurrentSelection;
import it.alessioricco.btc.models.HistoricalValue;
import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.models.MarketHistory;
import it.alessioricco.btc.models.Markets;
import it.alessioricco.btc.services.HistoryService;
import it.alessioricco.btc.services.MarketsService;
import it.alessioricco.btc.utils.BitcoinChartsUtils;
import it.alessioricco.btc.utils.Environment;
import it.alessioricco.btc.utils.ProgressDialogHelper;
import it.alessioricco.btc.utils.StringUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//import it.alessioricco.btc.models.MarketHistory;

/**
 * main activity of the app
 * TODO: detach the content_main and make it as a fragment
 */
final public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Chart.OnFragmentInteractionListener {

    // Container for subscriptions (RxJava). They will be unsubscribed onDestroy.
    protected CompositeSubscription compositeSubscription = new CompositeSubscription();
    @Inject
    MarketsService marketsService;
    @Inject
    HistoryService historyService;

    @InjectView(R.id.current)
    TextView currentValue;
    @InjectView(R.id.ask)
    TextView askValue;
    @InjectView(R.id.bid)
    TextView bidValue;
    @InjectView(R.id.high)
    TextView highValue;
    @InjectView(R.id.low)
    TextView lowValue;
    @InjectView(R.id.volume)
    TextView volume;
    @InjectView(R.id.avg)
    TextView avgValue;
    @InjectView(R.id.currencies)
    LinearLayout currenciesContainer;
    @InjectView(R.id.symbols)
    LinearLayout symbolsContainer;
    @InjectView(R.id.chart_fragment_container)
    LinearLayout chartFragmentContainer;

    @InjectView(R.id.content_main)
    RelativeLayout contentMain;

    @InjectView(R.id.latest_trade)
    TextView latestTrade;
    @InjectView(R.id.chart_progress)
    ProgressBar progressBar;

    private Markets markets = new Markets();

    private CurrentSelection currentSelection = new CurrentSelection();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //begin of the custom code
        ObjectGraphSingleton.getInstance().inject(this);
        ButterKnife.inject(this);
        // no values
        showEmptyMarket();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();


        switch (id) {
            case R.id.nav_src: {
                onNavigateSourceCode();
                break;
            }
            case R.id.nav_news: {
                onNavigateNews();
                break;
            }
            default: break;
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * open the source code github page
     */
    private void onNavigateSourceCode() {
        if (StringUtils.isNullOrEmpty(Environment.sourceCodeUrl)) {
            return;
        }

        final Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(Environment.sourceCodeUrl));
        startActivity(intent);
    }

    private void onNavigateNews() {
        final Intent intent = new Intent(this, NewsActivity.class);
        startActivity(intent);
    }

    /**
     * start the timer for updating the charts
     */
    void updateMarketsTimer() {
        final long delay = 2;
        final Observable<Long> observable = Observable.interval(delay, TimeUnit.MINUTES, Schedulers.io());

        // todo: check this snippet
        Subscription subscription = observable
                //.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        fetchData();
                    }
                });

        if (!compositeSubscription.isUnsubscribed()) {
            compositeSubscription.add(subscription);
        }
    }

    boolean firstTimeProgress = true;

    void startProgress() {
        if (firstTimeProgress) {
            ProgressDialogHelper.start(this);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }


    }

    void endProgress() {
        if (firstTimeProgress) {
            ProgressDialogHelper.end(this);
            firstTimeProgress = false;
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * read the markets from a JSON web service (bitcoincharts.com)
     *
     * @return
     */
    private Subscription asyncUpdateMarkets() {

        startProgress();
        final Observable<List<Market>> observable = marketsService.getMarkets();
        if (observable == null) {
            return null;
        }

        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                // on error return an empty list
                .onErrorReturn(new Func1<Throwable, List<Market>>() {
                    @Override
                    public List<Market> call(Throwable throwable) {
                        showEmptyMarket();
                        endProgress();
                        errorMessage();
                        return null;
                    }
                })
                .doOnNext(new Action1<List<Market>>() {
                    @Override
                    public void call(List<Market> markets) {
                        if (markets == null) {
                            return;
                        }
                        updateMarkets(markets);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        endProgress();
                    }
                })
                .subscribe();

    }

    /**
     * retieve the historica data for a given market and show them on a chart
     * @param currentMarket
     */

    private Observable<MarketHistory> getHistoryData(final Market currentMarket) {

        Observable<MarketHistory> observable;
        try {

            final String symbol = currentMarket.getSymbol();

            observable = this.historyService.getHistory(symbol)
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            progressBar.setVisibility(View.VISIBLE);
                            chartFragmentContainer.setVisibility(View.INVISIBLE);
                        }
                    })
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            progressBar.setVisibility(View.INVISIBLE);
                            chartFragmentContainer.setVisibility(View.VISIBLE);
                        }
                    })
                    .doOnNext(new Action1<MarketHistory>() {
                        @Override
                        public void call(MarketHistory marketHistory) {
                            drawChart(currentMarket, marketHistory);
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            progressBar.setVisibility(View.INVISIBLE);
                            chartFragmentContainer.setVisibility(View.VISIBLE);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();

            observable = Observable.error(e);
        }
        return observable;

    }


    private void fetchData() {
        compositeSubscription.add(asyncUpdateMarkets());
    }

    private void errorMessage() {

        Snackbar.make(this.contentMain,R.string.error_retrieving_data, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        fetchData();
                    }
                }).show();
    }

    /**
     * display the chart with the given history
     *
     * @param marketHistory
     */
    private void drawChart(final Market currentMarket, final MarketHistory marketHistory) {

        Chart chartFragment = (Chart) getSupportFragmentManager().findFragmentById(R.id.chart_fragment);

        if (chartFragment == null || marketHistory == null || ! marketHistory.hasValidData()) {
            chartFragment.showError(View.VISIBLE);
            return;
        }

        // check if the current value is present
        // if not is filled with the current value
        if (marketHistory.get(0) == null) {
            HistoricalValue currentValue = new HistoricalValue();
            currentValue.setValue(currentMarket.getClose());
            currentValue.setDate(currentMarket.getDate());
            currentValue.setAmount(0d);
            currentValue.setIndex(0);
            marketHistory.put(currentValue);
        }

        chartFragment.showError(View.GONE);
        chartFragment.update(marketHistory);

    }


    /**
     * populate the UI with empty methods
     */
    private void showEmptyMarket() {

        final String defaultEmptyTextValue  = "";
        final String defaultEmptyNumericValue  = getString(R.string.empty_numeric_value);
        final String defaultDataNotAvailable  = getString(R.string.data_not_available);

        // given the received model, draw the UI
        currentValue.setText(defaultEmptyTextValue);
        askValue.setText(defaultEmptyNumericValue);
        bidValue.setText(defaultEmptyNumericValue);
        highValue.setText(defaultEmptyNumericValue);
        lowValue.setText(defaultEmptyNumericValue);
        volume.setText(defaultEmptyNumericValue);

        avgValue.setText(defaultEmptyNumericValue);
        int color = Color.WHITE;
        avgValue.setTextColor(color);

        //TODO show a clock near the text
        latestTrade.setText(defaultDataNotAvailable);

    }

    /**
     * render the current market values on screen
     *
     * @param m
     */
    private Observable<Boolean> showCurrentMarket(final Market m) {

        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {


                if (m == null) {
                    return Observable.just(false);
                }

                // given the received model, draw the UI
                currentValue.setText(StringUtils.formatValue(m.getClose()));
                askValue.setText(StringUtils.formatValue(m.getAsk()));
                bidValue.setText(StringUtils.formatValue(m.getBid()));
                highValue.setText(StringUtils.formatValue(m.getHigh()));
                lowValue.setText(StringUtils.formatValue(m.getLow()));
                volume.setText(StringUtils.formatValue(m.getVolume()));

                Double percent = m.percent();
                avgValue.setText(StringUtils.formatPercentValue(percent));
                int color = Color.RED;
                if (percent > 0) {
                    color = Color.GREEN;
                } else if (percent == 0) {
                    color = Color.WHITE;
                }
                avgValue.setTextColor(color);

                //TODO show a clock near the text
                latestTrade.setText((new PrettyTime()).format(m.getDate()));

                return Observable.just(true);
            }

        });
    }

    /**
     * change the style to an horizontal scrollbar with list of list_of_symbols_main or currencies
     *
     * @param layout
     * @param value
     */
    private Observable<Boolean> applySelectionToContainer(final LinearLayout layout, final String value) {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {

                final Context context = getApplicationContext();
                final String valueToSearch = getString(R.string.string_space, value.toUpperCase());
                for (int i = 0; i < layout.getChildCount(); i++) {
                    final View v = layout.getChildAt(i);
                    if (v instanceof TextView) {
                        final TextView t = (TextView) v;
                        final String text = t.getText().toString();
                        final int resource = text.equals(valueToSearch) ? R.color.SelectedCurrencyItem : R.color.UnselectedCurrencyItem;
                        final int color = ContextCompat.getColor(context, resource);
                        t.setTextColor(color);
                    }
                }

                return Observable.just(true);
            }
        });
    }

    /**
     * called everytime a currency is selected on screen
     */
    private void onSelectedCurrency() {
        final String currency = currentSelection.getCurrentMarketCurrency();

        this.markets.getSymbolsAsObservable(currency)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        symbolsContainer.removeAllViews();
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(final String currentSymbol) {
                        //final String currentSymbol = symbol;
                        final TextView systemTextView = (TextView) getLayoutInflater().inflate(R.layout.currency_template, null);
                        systemTextView.setText(getString(R.string.string_space, currentSymbol));
                        systemTextView.setTag(currentSymbol);
                        systemTextView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Log.e("Tag", "clicked on " + systemTextView.getText());
                                currentSelection.setCurrentMarketSymbol(currentSymbol);
                                onSelectedSymbol();
                            }
                        });
                        symbolsContainer.addView(systemTextView);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        onSelectedSymbol();
                    }
                })
                .subscribe();

    }


    /**
     * when a symbol is selected, we fetch current data and history
     */
    private void onSelectedSymbol() {

        String symbol = currentSelection.getCurrentMarketSymbol();
        final String currency = currentSelection.getCurrentMarketCurrency();
        final Market selectedMarket = markets.getMarket(currency, symbol);
        if (selectedMarket == null) {
            return;
        }

        if (symbol == null && !StringUtils.isNullOrEmpty(selectedMarket.getSymbol())) {
            currentSelection.setCurrentMarketSymbol(BitcoinChartsUtils.normalizeSymbolName(selectedMarket.getSymbol()));
            symbol = currentSelection.getCurrentMarketSymbol();
        }

        //todo: verify is <Boolean> is really needed
        Observable<Boolean> applyCurrencySelectionToUI = applySelectionToContainer(this.currenciesContainer, currency);
        Observable<Boolean> applySymbolSelectionToUI = applySelectionToContainer(this.symbolsContainer, symbol);
        Observable<MarketHistory> getHistoryData = getHistoryData(selectedMarket);
        Observable<Boolean> showCurrentMarket = showCurrentMarket(selectedMarket);

        // parallelize
        Subscription subscription = Observable.merge(applyCurrencySelectionToUI,
                applySymbolSelectionToUI, getHistoryData, showCurrentMarket).subscribe();

        compositeSubscription.add(subscription);

    }

    public void populateCurrencyList() {
        // fill the currencies scrollView
        this.currenciesContainer.removeAllViews();

        Subscription subscription = this.markets.getCurrenciesAsObservable().doOnNext(new Action1<String>() {
            @Override
            public void call(final String currentCurrency) {
                final TextView currencyTextView = (TextView) getLayoutInflater().inflate(R.layout.currency_template, null);
                currencyTextView.setText(getString(R.string.string_space, currentCurrency));
                currencyTextView.setTag(currentCurrency);
                currencyTextView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        currentSelection.setCurrentMarketCurrency(currentCurrency);
                        onSelectedCurrency();
                    }
                });
                currenciesContainer.addView(currencyTextView);
            }
        }).subscribe();

        compositeSubscription.add(subscription);
    }

    /**
     * called everytime the list of market tickers should be updated
     *
     * @param newMarkets
     */
    public void updateMarkets(final List<Market> newMarkets) {

        this.markets.setMarkets(newMarkets);
        populateCurrencyList();
        onSelectedCurrency();

    }

    /**
     * https://github.com/ReactiveX/RxJava/wiki/The-RxJava-Android-Module
     */
    @Override
    public void onResume() {
        super.onResume();

        compositeSubscription.add(asyncUpdateMarkets());
        updateMarketsTimer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeSubscription.unsubscribe();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse(Environment.authorUrl))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

package it.alessioricco.btc;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ocpsoft.pretty.time.PrettyTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.CurrentSelection;
import it.alessioricco.btc.models.HistoricalValue;
import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.models.MarketHistory;
import it.alessioricco.btc.models.Markets;
import it.alessioricco.btc.services.MarketsService;
import it.alessioricco.btc.utils.BitcoinChartsUtils;
import it.alessioricco.btc.utils.Environment;
import it.alessioricco.btc.utils.ProgressDialogHelper;
import it.alessioricco.btc.utils.StringUtils;
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import st.lowlevel.storo.Storo;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * main activity of the app
 * TODO: detach the content_main and make it as a fragment
 */
final public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Container for subscriptions (RxJava). They will be unsubscribed onDestroy.
    protected CompositeSubscription compositeSubscription = new CompositeSubscription();
    @Inject
    MarketsService marketsService;
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
    @InjectView(R.id.chart)
    lecho.lib.hellocharts.view.LineChartView chart;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * start the timer for updating the charts
     */
    void updateMarketsTimer() {
        long delay = 5;
        Observable<Long> observable = Observable.interval(delay, TimeUnit.MINUTES, Schedulers.io());

        Subscription subscription =  observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        asyncUpdateMarkets();
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
     * @return
     */
    private Subscription asyncUpdateMarkets() {

        //todo the progress must me show only the first time, then we should use another thing
        startProgress();
        final Observable<List<Market>> observable = marketsService.getMarkets();

        return observable
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Market>>() {
                    @Override
                    public void onCompleted() {
                        // todo hide the spinner
                        endProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            int code = response.code();
                            //TODO: add a toast
                            //TODO: retry if it doesn't works
                        }
                    }

                    @Override
                    public void onNext(List<Market> markets) {
                        updateMarkets(markets);
                    }
                });
    }

    private void getHistoricalData(final String symbol){
        try {

            //TODO: chart must be a fragment
            chart.setVisibility(View.GONE);

            // if data are cached we don't need of a progress bar
            final Boolean expired = Storo.hasExpired(symbol).execute();
            final boolean isCached = expired != null && expired == false;
            if (!isCached) {
                progressBar.setVisibility(View.VISIBLE);
            }

            //TODO would be great to know if data came from cache or not (and apply a progress)
            final Observable<MarketHistory> observable = this.marketsService.getHistory(symbol);
            final Subscription history = observable
                    .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MarketHistory>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            // cast to retrofit.HttpException to get the response code
                            if (e instanceof HttpException) {
                                HttpException response = (HttpException) e;
                                int code = response.code();
                                //TODO: add a toast
                            }
                        }

                        @Override
                        public void onNext(MarketHistory history) {
                            // if history is null we keep the chart invisible
                            if (history != null) {
                                //TODO: double check on the currency/action to avoid wasting time on old selections
                                drawChart(history);
                                chart.setVisibility(View.VISIBLE);
                                if (!isCached) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    });
            compositeSubscription.add(history);
        } catch (IOException e) {
            Log.e("IOError",e.getLocalizedMessage());
        }
    }

    /**
     * display the chart with the given history
     * @param marketHistory
     */
    private void drawChart(MarketHistory marketHistory) {

        if (marketHistory == null) {
            return;
        }

        final List<HistoricalValue> history = marketHistory.getHistory();

        if (history == null || history.size() < 2) {
            return;
        }

        final List<PointValue> values = new ArrayList<PointValue>();

        final int steps = Environment.chartSteps;
        final int step = (history.size() < steps) ? 1 : history.size()/steps;

        float minValue = Float.MAX_VALUE;
        float maxValue = Float.MIN_VALUE;

        for(int i = 0; i< history.size(); i+=step ){
            final HistoricalValue historicalValue = history.get(i);
            float value = historicalValue.getValue().floatValue();

            // if the currency has values with 6 digits
            // we can use less digits to improve the ui
            if (value > 1000000) {
                value = value / 1000;
            }

            if (value < minValue) minValue = value;
            if (value > maxValue) maxValue = value;

            //TODO: add labels to X axis
            values.add(new PointValue(i, value));
        }

        final Line line = new Line(values)
                .setColor(Environment.chartLineColor)
                .setHasPoints(true)
                .setPointRadius(Environment.chartLinePointRadius)
                .setCubic(true);
        final List<Line> lines = new ArrayList<Line>();
        lines.add(line);
        final LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lines);

        final Axis axisY = new Axis().setHasLines(true).setLineColor(Environment.chartAxisColor);
        final Axis axisX = new Axis().setHasLines(true).setLineColor(Environment.chartAxisColor);

        axisY.setFormatter(new SimpleAxisValueFormatter());
        axisX.setAutoGenerated(false);

        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);
        lineChartData.setBaseValue(minValue);

        chart.setLineChartData(lineChartData);
        chart.setVisibility(View.VISIBLE);

    }

    /**
     * render the current market values on screen
     * @param m
     */
    private void showCurrentMarket(final Market m) {
        if (m == null) return;

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

        // retrieve history and display on screen
        getHistoricalData(m.getSymbol());

    }

    /**
     * change the style to an horizontal scrollbar with list of list_of_symbols_main or currencies
     * @param layout
     * @param value
     */
    private void applySelectionToContainer(final LinearLayout layout, final String value){
        final Context context = getApplicationContext();
        final String valueToSearch = getString(R.string.string_space, value.toUpperCase());
        for (int i=0; i<layout.getChildCount(); i++) {
            final View v = layout.getChildAt(i);
            if ( v instanceof TextView ) {
                final TextView t = (TextView) v;
                final String text = t.getText().toString();
                final int resource = text.equals(valueToSearch) ? R.color.SelectedCurrencyItem : R.color.UnselectedCurrencyItem;
                final int color = ContextCompat.getColor(context, resource);
                t.setTextColor(color);
            }
        }
    }

    /**
     * called everytime a currency is selected on screen
     */
    private void onSelectedCurrency() {
        final String currency = currentSelection.getCurrentMarketCurrency();
        final List<String> symbols = this.markets.getSymbols(currency);

        // fill the currencies scrollView
        this.symbolsContainer.removeAllViews();

        for (String symbol: symbols) {
            final String currentSymbol = symbol;
            final TextView systemTextView = (TextView)getLayoutInflater().inflate(R.layout.currency_template, null);
            systemTextView.setText(getString(R.string.string_space, symbol));
            systemTextView.setTag(symbol);
            systemTextView.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    Log.e("Tag","clicked on "+systemTextView.getText());
                    currentSelection.setCurrentMarketSymbol(currentSymbol);
                    onSelectedSymbol();
                }
            });
            this.symbolsContainer.addView(systemTextView);
        }

        onSelectedSymbol();
    }

    /**
     * give the current symbol and the current currency
     * display the market on the screen
     */
    private void onSelectedSymbol() {
        chart.setVisibility(View.INVISIBLE);

        String symbol = currentSelection.getCurrentMarketSymbol();
        final String currency = currentSelection.getCurrentMarketCurrency();
        final Market selectedMarket = markets.getMarket(currency, symbol);
        if (selectedMarket != null) {

            if (symbol == null && ! StringUtils.isNullOrEmpty( selectedMarket.getSymbol())) {
                currentSelection.setCurrentMarketSymbol(BitcoinChartsUtils.normalizeSymbolName(selectedMarket.getSymbol()));
                symbol = currentSelection.getCurrentMarketSymbol();
            }

            showCurrentMarket(selectedMarket);
            applySelectionToContainer(this.currenciesContainer, currency);
            applySelectionToContainer(this.symbolsContainer, symbol);
        }


    }

    /**
     * called everytime the list of market tickers should be updated
     * @param newMarkets
     */
    private void updateMarkets(final List<Market> newMarkets) {

        this.markets.setMarkets(newMarkets);

        //TODO: validate the markets structure

        // fill the currencies scrollView
        this.currenciesContainer.removeAllViews();

        for (String currency: this.markets.getCurrencies()) {
            final String currentCurrency = currency;
            final TextView currencyTextView = (TextView)getLayoutInflater().inflate(R.layout.currency_template, null);
            currencyTextView.setText(getString(R.string.string_space, currency));
            currencyTextView.setTag(currency);
            currencyTextView.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    Log.e("Tag","clicked on "+currencyTextView.getText());
                    currentSelection.setCurrentMarketCurrency(currentCurrency);
                    onSelectedCurrency();
                }
            });
            this.currenciesContainer.addView(currencyTextView);
        }

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
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
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
}

package it.alessioricco.btc;

import android.content.Context;
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
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.models.CurrentSelection;
import it.alessioricco.btc.models.Market;
import it.alessioricco.btc.models.Markets;
import it.alessioricco.btc.services.MarketsService;
import it.alessioricco.btc.utils.BitcoinChartsUtils;
import it.alessioricco.btc.utils.StringUtils;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * main activity of the app
 * TODO: detach the content_main and make it as a fragment
 */
final public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

    @InjectView(R.id.currencies)
    LinearLayout currenciesContainer;
    @InjectView(R.id.symbols)
    LinearLayout symbolsContainer;

    // Container for subscriptions (RxJava). They will be unsubscribed onDestroy.
    //TODO: check, could be a dead code
    protected CompositeSubscription compositeSubscription = new CompositeSubscription();

    private Markets markets = new Markets();

//    private String currentMarketSymbol = "";
//    private String currentMarketCurrency = "";

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
     * read the markets from a JSON web service (bitcoincharts.com)
     * @return
     */
    private Subscription asyncUpdateMarkets() {
        Observable<List<Market>> observable = marketsService.getMarkets();

        return observable
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Market>>() {
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
                    public void onNext(List<Market> markets) {
                        updateMarkets(markets);
                    }
                });
    }

    /**
     * render the current market values on screen
     * @param m
     */
    private void showCurrentMarket(final Market m) {
        if (m == null) return;

        // given the received model, draw the UI
        currentValue.setText(StringUtils.formatValue(m.getBid()));
        askValue.setText(StringUtils.formatValue(m.getAsk()));
        bidValue.setText(StringUtils.formatValue(m.getBid()));
        highValue.setText(StringUtils.formatValue(m.getHigh()));
        lowValue.setText(StringUtils.formatValue(m.getLow()));
        volume.setText(StringUtils.formatValue(m.getVolume()));



        //String vv = new SimpleDateFormat("MM dd, yyyy hh:mma").format(df);
    }

    /**
     * change the style to an horizontal scrollbar with list of symbols or currencies
     * @param layout
     * @param value
     */
    private void applySelectionToContainer(final LinearLayout layout, final String value){
        final Context context = getApplicationContext();
        final String valueToSearch = getString(R.string.string_space, value.toUpperCase());
        for (int i=0; i<layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if ( v instanceof TextView ) {
                final TextView t = (TextView) v;
                final String text = t.getText().toString();
                //TODO use an inline conditional
                int color;
                if (text.equals(valueToSearch)) {
                     color = ContextCompat.getColor(context, R.color.SelectedCurrencyItem);
                } else {
                    color = ContextCompat.getColor(context, R.color.UnselectedCurrencyItem);
                }
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

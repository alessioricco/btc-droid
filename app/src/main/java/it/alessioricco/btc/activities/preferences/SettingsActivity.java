package it.alessioricco.btc.activities.preferences;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;

import java.util.List;

import it.alessioricco.btc.R;
import it.alessioricco.btc.models.Markets;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static rx.Observable.merge;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    public static final String ARG_MARKET = "market";
    private static Markets market;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {


            // For all other preferences, set the summary to the value's
            // simple string representation.
            if (preference instanceof CheckBoxPreference) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                if (value instanceof Boolean) {
                    final String summary = ((Boolean) value) ? "enabled" : "disabled";
                    preference.setSummary(summary);
                    return true;
                }
            }

            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceCurrencyToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), true));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        final Markets m = (Markets) getIntent().getSerializableExtra(SettingsActivity.ARG_MARKET);
        if (m != null) {
            market = m;
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || CurrencyPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CurrencyPreferenceFragment extends PreferenceFragment {

        final protected CompositeSubscription compositeSubscription = new CompositeSubscription();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_currency);
            initialize();
        }

        private void initialize() {

            if (market == null) {
                return;
            }

            final Context context = this.getActivity();
            if (context == null) {
                return;
            }

            //todo: move to PreferenceFragment
            final PreferenceManager preferenceManager = getPreferenceManager();
            if (preferenceManager == null) {
                return;
            }

            final PreferenceScreen preferenceScreen = preferenceManager.createPreferenceScreen(context);
            if (preferenceScreen == null) {
                return;
            }

            final PreferenceCategory category = new PreferenceCategory(context);
            if (category == null) {
                return;
            }

            category.setTitle("Currencies");
            preferenceScreen.addPreference(category);

            final Observable createCheckBoxSubscription = market.getCurrenciesAsObservable()
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(final String currency) {

                            final CheckBoxPreference checkBoxPref = new CheckBoxPreference(context);
                            checkBoxPref.setTitle(currency);
                            checkBoxPref.setSummary("");
                            checkBoxPref.setKey(currency);
                            checkBoxPref.setChecked(true);
                            category.addPreference(checkBoxPref);

                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            setPreferenceScreen(preferenceScreen);
                        }
                    });

            final Observable createBindingsSubscription = market.getCurrenciesAsObservable()
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(final String currency) {

                            final Preference preference = findPreference(currency);
                            if (preference == null) {
                                return;
                            }
                            bindPreferenceCurrencyToValue(preference);

                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            setPreferenceScreen(preferenceScreen);
                        }
                    });

            Subscription subscription = Observable.merge(createCheckBoxSubscription,createBindingsSubscription).subscribe();

            compositeSubscription.add(subscription);

        }
    }



}

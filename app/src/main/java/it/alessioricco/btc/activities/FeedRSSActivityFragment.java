package it.alessioricco.btc.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.alessioricco.btc.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedRSSActivityFragment extends Fragment {

    public FeedRSSActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_rss, container, false);
    }
}

package it.alessioricco.btc.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.alessioricco.btc.R;

/**
 * Created by alessioricco on 12/10/2016.
 */

public class ProgressDialogHelper {

    private static void enableDisableView(View view, boolean enabled) {
        // http://stackoverflow.com/questions/7687784/how-to-disable-enable-all-children-on-linearlayout-in-android
            view.setEnabled(enabled);

            if ( view instanceof ViewGroup ) {
                ViewGroup group = (ViewGroup)view;

                for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                    enableDisableView(group.getChildAt(idx), enabled);
                }
            }
        }

    private static ViewGroup getMainView(final Activity activity) {
        // http://stackoverflow.com/questions/7776768/android-what-is-android-r-id-content-used-for
        return (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public static void start(final Activity activity) {

        final ViewGroup viewGroup = getMainView(activity);
        enableDisableView(viewGroup, false);

        LayoutInflater inflater = LayoutInflater.from(activity);
        if (inflater != null) {
            inflater.inflate(R.layout.progress_fullscreen, viewGroup);
            //final View progressBackground = viewGroup.findViewById(R.id.progress_fullscreen);
        }

    }

    public static void end(final Activity activity) {

        final ViewGroup viewGroup = getMainView(activity);
        enableDisableView(viewGroup, true);

        final View progressBackground = viewGroup.findViewById(R.id.progress_fullscreen);
        if (progressBackground != null) {
            viewGroup.removeView(progressBackground);
        }
    }

}

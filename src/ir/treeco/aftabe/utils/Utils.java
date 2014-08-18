package ir.treeco.aftabe.utils;

import android.view.View;

/**
 * Created by hamed on 8/12/14.
 */
public class Utils {
    public static void toggleVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);
    }
}

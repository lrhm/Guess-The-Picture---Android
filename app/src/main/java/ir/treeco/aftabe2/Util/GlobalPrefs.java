package ir.treeco.aftabe2.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by al on 5/31/16.
 */
public class GlobalPrefs {

    private static final String APP_SHARED_PREFS = "globalPrefs"; //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;

    private static Object lock = new Object();
    private static GlobalPrefs instance;

    public static GlobalPrefs getInstance(Context context) {
        synchronized (lock) {
            if (instance == null)
                instance = new GlobalPrefs(context);
            return instance;
        }

    }

    private GlobalPrefs(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPrefs() {
        return _sharedPrefs;
    }
}

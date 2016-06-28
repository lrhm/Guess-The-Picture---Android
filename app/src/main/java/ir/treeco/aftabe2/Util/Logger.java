package ir.treeco.aftabe2.Util;

import android.util.Log;

/**
 * Created by al on 6/5/16.
 */
public class Logger {

    private static boolean debug = true;


    public static boolean isDebug() {
        return debug;
    }

    public static void i(String tag, String msg) {

        if (debug)
            Log.i(tag, msg);

    }

    public static void d(String tag, String msg) {

        if (debug)
            Log.d(tag, msg);

    }

    public static void v(String tag, String msg) {

        if (debug)
            Log.v(tag, msg);

    }

    public static void e(String tag, String msg) {

        if (debug)
            Log.e(tag, msg);

    }

    public static void w(String tag, String msg) {

        if (debug)
            Log.w(tag, msg);

    }

    public static void e(String tag, String msg, Throwable t) {

        if (debug)
            Log.e(tag, msg, t);

    }
}

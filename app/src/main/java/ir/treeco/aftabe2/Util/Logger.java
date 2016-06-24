package ir.treeco.aftabe2.Util;

import android.util.Log;

/**
 * Created by al on 6/5/16.
 */
public class Logger {

    private static boolean debug = true;

    private static boolean test = true;

    public static boolean isTest() {
        return test;
    }

    private static final String testUrl = "http://server.pakoo.ir:2020/";
    private static final String baseUrl = "https://aftabe2.com:2020/";

    public static String getUrl() {
        if (test)
            return testUrl;
        return baseUrl;
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

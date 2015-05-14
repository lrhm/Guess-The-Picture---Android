package ir.treeco.aftabe;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: hamed
 * Date: 3/2/14
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoinManager {
    public static final int LEVEL_COMPELETED_PRIZE = 30;
    public static final int ALPHABET_HIDING_COST = 40;
    public static final int LETTER_REVEAL_COST = 50;
    public static final int SKIP_LEVEL_COST = 130;
    private static final String TAG = "CoinManager";


    public static interface CoinsChangedListener {
        void changed(int newAmount);
    }

    public static boolean spendCoins(int amount, SharedPreferences preferences) {
        int nextAmount = getCoinsCount(preferences) - amount;
        if (nextAmount < 0)
            return false;
        setCoinsCount(nextAmount, preferences);
        return true;
    }

    public static void earnCoins(int amount, SharedPreferences preferences) {
        int nextAmount = getCoinsCount(preferences) + amount;
        setCoinsCount(nextAmount, preferences);
    }

    public static int getCoinsCount(SharedPreferences preferences) {
        return preferences.getInt(COINS_COUNT_TAG, 200);
    }

    private static String COINS_COUNT_TAG = "COINS_COUNT";
    private static void setCoinsCount(int nextAmount, SharedPreferences preferences) {
        if (!preferences.edit().putInt(COINS_COUNT_TAG, nextAmount).commit()) {
            Log.e(TAG, "Could not store coins count!");
        }

        if (listener != null)
            listener.changed(nextAmount);
    }

    private static CoinsChangedListener listener;

    public static void setCoinsChangedListener(CoinsChangedListener listener, SharedPreferences prefrences) {
        CoinManager.listener = listener;
        listener.changed(getCoinsCount(prefrences));
    }
}

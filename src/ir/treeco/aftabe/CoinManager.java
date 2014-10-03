package ir.treeco.aftabe;

import android.content.SharedPreferences;

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
        return preferences.getInt(tag, 200);
    }

    private static String tag = "COINS_COUNT";
    private static void setCoinsCount(int nextAmount, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(tag, nextAmount);
        editor.commit();
        if (listener != null)
            listener.changed(nextAmount);
    }

    private static CoinsChangedListener listener;

    public static void setCoinsChangedListener(CoinsChangedListener listener, SharedPreferences prefrences) {
        CoinManager.listener = listener;
        listener.changed(getCoinsCount(prefrences));
    }
}

package ir.treeco.aftabe.New;

import android.content.Context;

import ir.treeco.aftabe.New.Adapter.DBAdapter;

/**
 * Created by behdad on 8/23/15.
 */

public class CoinManager {
    public static final int LEVEL_COMPELETED_PRIZE = 30;
    public static final int ALPHABET_HIDING_COST = 40;
    public static final int LETTER_REVEAL_COST = 50;
    public static final int SKIP_LEVEL_COST = 130;
    private static final String TAG = "CoinManager";
    private DBAdapter db;

    public CoinManager(Context context) {
        db = DBAdapter.getInstance(context);
    }

    public interface CoinsChangedListener {
        void changed(int newAmount);
    }

    public boolean spendCoins(int amount) {
        int nextAmount = getCoinsCount() - amount;
        if (nextAmount < 0)
            return false;
        setCoinsCount(nextAmount);
        return true;
    }

    public void earnCoins(int amount) {
        int nextAmount = getCoinsCount() + amount;
        setCoinsCount(nextAmount);
    }

    public int getCoinsCount() {
        return db.getCoins();
    }

    private void setCoinsCount(int nextAmount) {
        db.updateCoins(nextAmount);

        if (listener != null)
            listener.changed(nextAmount);
    }

    private static CoinsChangedListener listener;

    public void setCoinsChangedListener(CoinsChangedListener listener) {
        CoinManager.listener = listener;
        listener.changed(getCoinsCount());
    }
}

package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.widget.Toast;

import ir.treeco.aftabe.New.View.Custom.ToastMaker;
import ir.treeco.aftabe.R;

public class CoinAdapter {
    public static final int LEVEL_COMPELETED_PRIZE = 30;
    public static final int ALPHABET_HIDING_COST = 40;
    public static final int LETTER_REVEAL_COST = 50;
    public static final int SKIP_LEVEL_COST = 130;
    private static final String TAG = "CoinManager";
    private DBAdapter db;
    private Context context;

    public CoinAdapter(Context context) {
        db = DBAdapter.getInstance(context);
        this.context = context;
    }

    public interface CoinsChangedListener {
        void changed(int newAmount);
    }

    public boolean spendCoins(int amount) {
        int nextAmount = getCoinsCount() - amount;
        if (nextAmount < 0) {
            ToastMaker.show(context, context.getString(R.string.not_enought_coins), Toast.LENGTH_SHORT);
            return false;
        }
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
        CoinAdapter.listener = listener;
        listener.changed(getCoinsCount());
    }
}

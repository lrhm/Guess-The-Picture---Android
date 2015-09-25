package ir.treeco.aftabe.Adapter;

import android.content.Context;
import android.widget.Toast;

import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Custom.ToastMaker;
import ir.treeco.aftabe.R;

public class CoinAdapter {
    public static final int LEVEL_COMPELETED_PRIZE = 30;
    public static final int ALPHABET_HIDING_COST = 40;
    public static final int LETTER_REVEAL_COST = 50;
    public static final int SKIP_LEVEL_COST = 130;
    private DBAdapter db;
    private Context context;
    private Tools tools;

    public CoinAdapter(Context context) {
        this.context = context;
        db = DBAdapter.getInstance(context);
        tools = new Tools(context);
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
        tools.backUpDB();

        if (listener != null)
            listener.changed(nextAmount);
    }

    private static CoinsChangedListener listener;

    public void setCoinsChangedListener(CoinsChangedListener listener) {
        CoinAdapter.listener = listener;
        listener.changed(getCoinsCount());
    }
}

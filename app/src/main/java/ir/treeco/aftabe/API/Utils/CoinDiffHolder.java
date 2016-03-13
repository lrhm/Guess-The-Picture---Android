package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/13/16.
 */
public class CoinDiffHolder {

    @Expose
    int coins;

    public CoinDiffHolder(int diff) {
        coins = diff;
    }
}

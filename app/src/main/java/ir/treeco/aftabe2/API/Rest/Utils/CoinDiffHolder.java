package ir.treeco.aftabe2.API.Rest.Utils;

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

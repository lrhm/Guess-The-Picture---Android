package ir.treeco.aftabe.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by al on 3/5/16.
 */
public class SaveHolder {

    @Expose
    @SerializedName("th")
    TokenHolder tokenHolder;

    @Expose
    @SerializedName("k")
    String key;

    public SaveHolder(TokenHolder tokenHolder, String key) {
        this.key = key;
        this.tokenHolder = tokenHolder;
    }

    public TokenHolder getTokenHolder() {
        return tokenHolder;
    }

    public String getKey() {
        return key;
    }
}

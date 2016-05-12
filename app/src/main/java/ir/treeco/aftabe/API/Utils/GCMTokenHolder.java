package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 4/25/16.
 */
public class GCMTokenHolder {

    @Expose
    String gcmToken;

    public GCMTokenHolder(String gcmToken) {
        this.gcmToken = gcmToken;
    }
}

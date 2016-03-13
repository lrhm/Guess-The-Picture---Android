package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe.Util.Tools;

/**
 * Created by al on 3/4/16.
 */
public class GuestCreateToken {

    @Expose
    boolean guest = true;

    @Expose
    String imei ;

    @Expose
    double seed;

    public GuestCreateToken(String imei) {
        this.imei = imei;
        seed = Tools.getSeed();
    }
}

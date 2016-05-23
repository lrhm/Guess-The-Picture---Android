package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe2.Util.Tools;

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

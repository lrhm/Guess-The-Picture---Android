package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/4/16.
 */
public class GuestCreateToken {

    @Expose
    boolean guest = true;

    @Expose
    String imei ;

    public GuestCreateToken(String imei) {
        this.imei = imei;
    }
}

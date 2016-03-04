package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by al on 3/4/16.
 */
public class GoogleToken {

    @Expose
    @SerializedName("gToken")
    public String gToken;

    @Expose
    @SerializedName("imei")
    public String imei;

    public GoogleToken(String gToken, String imei) {
        this.gToken = gToken;
        this.imei = imei;
    }



}



package ir.treeco.aftabe.API.Utils;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ir.treeco.aftabe.Object.TokenHolder;
import ir.treeco.aftabe.Util.RandomString;
import ir.treeco.aftabe.Util.Tools;

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

    @Expose
    public String name;

    @Expose
    public String guestID;

    @Expose
    public double seed;

    public GoogleToken(String gToken) {
        this.gToken = gToken;
        this.imei = RandomString.nextString();
        seed = Tools.getSeed();
    }

    public void setUsername(String username) {
        this.name = username;
        setGuestID();
    }

    private void setGuestID() {
        if (!Tools.isUserRegistered()) {
            return;
        }
        TokenHolder tokenHolder = Tools.getTokenHolder();
        if (tokenHolder == null)
            return;
        if (!tokenHolder.isGuest())
            return;
        LoginInfo loginInfo = tokenHolder.getLoginInfo();
        if(loginInfo != null)
        guestID = loginInfo.getUserId();
    }
}



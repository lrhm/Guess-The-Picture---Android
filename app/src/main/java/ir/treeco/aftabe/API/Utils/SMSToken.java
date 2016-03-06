package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe.Object.TokenHolder;
import ir.treeco.aftabe.Util.Tools;

/**
 * Created by al on 3/4/16.
 */
public class SMSToken {

    @Expose
    public String phone;

    @Expose
    public String created;

    @Expose
    public String code;

    @Expose
    public String smsTokenId;

    public String id;

    @Expose
    public String imei;

    @Expose
    public String name;

    @Expose
    public String guestID;

    public void update(String imei, String name, String code) {
        this.imei = imei;
        this.name = name;
        this.smsTokenId = id;
        this.code = code;
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
        if (loginInfo != null)
            guestID = loginInfo.getUserId();
    }
}

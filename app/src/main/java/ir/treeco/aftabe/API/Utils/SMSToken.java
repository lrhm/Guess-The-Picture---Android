package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe.Object.TokenHolder;
import ir.treeco.aftabe.Util.RandomString;
import ir.treeco.aftabe.Util.Tools;

/**
 * Created by al on 3/4/16.
 */
public class SMSToken {

    @Expose
    public String phone;

    @Expose
    public String smsTokenId;

    @Expose
    public String imei;

    @Expose
    public String name;

    @Expose
    public String guestID;

    public void update(SMSValidateToken smsValidateToken , String checkedUsername) {
        this.imei = RandomString.nextString();
        this.smsTokenId = smsValidateToken.getId();
        this.name = checkedUsername;
        this.phone = smsValidateToken.getPhone();
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

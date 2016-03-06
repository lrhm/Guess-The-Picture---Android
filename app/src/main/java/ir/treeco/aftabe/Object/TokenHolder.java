package ir.treeco.aftabe.Object;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.API.Utils.GuestCreateToken;
import ir.treeco.aftabe.API.Utils.LoginInfo;
import ir.treeco.aftabe.API.Utils.SMSToken;

/**
 * Created by al on 3/5/16.
 */
public class TokenHolder {

    private static final String TAG = "TokenHolder";


    @Expose
    @SerializedName("tk")
    String loginInfo;

    @Expose
    @SerializedName("t")
    boolean isGuest;


    public  TokenHolder(User myUser) {

        this.isGuest = myUser.isGuest();

        Gson gson = new Gson();
        this.loginInfo = gson.toJson(myUser.getLoginInfo());
    }

    public boolean isGuest() {
        return isGuest;
    }

    public LoginInfo getLoginInfo() {

        Gson gson = new Gson();
        try {
            return gson.fromJson(loginInfo, LoginInfo.class);

        } catch (Exception e) {

            return null;
        }
    }


}

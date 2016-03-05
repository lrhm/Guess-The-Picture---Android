package ir.treeco.aftabe.Object;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.API.Utils.GuestCreateToken;
import ir.treeco.aftabe.API.Utils.SMSToken;

/**
 * Created by al on 3/5/16.
 */
public class TokenHolder {

    private static final String TAG = "TokenHolder";
    public static final int TOKEN_TYPE_GUEST = 1;
    public static final int TOKEN_TYPE_SMS = 2;
    public static final int TOKEN_TYPE_GOOGLE = 4;

    @Expose
    @SerializedName("tk")
    String gsonToken;

    @Expose
    @SerializedName("t")
    int tokenType;

    private void init(String gsonToken, int tokenType) {
        this.tokenType = tokenType;
        this.gsonToken = gsonToken;
    }

    public TokenHolder(SMSToken smsToken) {
        Gson gson = new Gson();
        String gsonString = gson.toJson(smsToken);
        Log.d(TAG, gsonString + " type 2");
        init(gsonString, TOKEN_TYPE_SMS);
    }

    public TokenHolder(GoogleToken googleToken) {
        Gson gson = new Gson();
        String gsonString = gson.toJson(googleToken);
        Log.d(TAG, gsonString + " type 4");
        init(gsonString, TOKEN_TYPE_GOOGLE);
    }

    public TokenHolder(GuestCreateToken guestCreateToken) {
        Gson gson = new Gson();
        String gsonString = gson.toJson(guestCreateToken);
        Log.d(TAG, gsonString + " type 1");
        init(gsonString, TOKEN_TYPE_GUEST);
    }

    public int getType() {
        return tokenType;
    }

    public SMSToken getSMSToken() {
        if (tokenType != TOKEN_TYPE_SMS)
            throw new IllegalStateException();
        Gson gson = new Gson();
        return gson.fromJson(gsonToken, SMSToken.class);
    }


    public GoogleToken getGoogleToken() {
        if (tokenType != TOKEN_TYPE_GOOGLE)
            throw new IllegalStateException();
        Gson gson = new Gson();
        return gson.fromJson(gsonToken, GoogleToken.class);
    }

    public GuestCreateToken getGuestCreateToken() {
        if (tokenType != TOKEN_TYPE_GUEST)
            throw new IllegalStateException();
        Gson gson = new Gson();
        return gson.fromJson(gsonToken, GuestCreateToken.class);
    }


}

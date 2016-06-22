package ir.treeco.aftabe2.Object;

import ir.treeco.aftabe2.Util.Logger;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;

import ir.treeco.aftabe2.Util.Encryption;
import ir.treeco.aftabe2.Util.StoreAdapter;

/**
 * Created by al on 3/5/16.
 */
public class SaveHolder {

    @Expose
    @SerializedName("th")
    String tokenHolder;

    @Expose
    @SerializedName("k")
    String key;


    @Expose
    @SerializedName("1")
    boolean one;

    @Expose
    @SerializedName("2")
    boolean two;


    public SaveHolder(TokenHolder tokenHolder, String key) {

        Gson gson = new Gson();

        this.key = Encryption.encryptAES(key, getAESKey());
        this.tokenHolder = Encryption.encryptAES(gson.toJson(tokenHolder), getAESKey());
        this.one = StoreAdapter.isInstaUsed();
        this.two = StoreAdapter.isTelegramUsed();

    }

    public TokenHolder getTokenHolder() {

        String jsonString = Encryption.decryptAES(tokenHolder, getAESKey());
        Gson gson = new Gson();
        return gson.fromJson(jsonString, TokenHolder.class);
    }

    public String getKey() {

        return Encryption.decryptAES(key, getAESKey());

    }

    public boolean getInstaUsed(){
        return one;
    }

    public boolean getTelegramUsed(){
        return two;
    }

    public byte[] getAESKey() {


        String str = "aftabe is a awesome game";
        try {
            byte[] key = new byte[16];
            byte[] strBytes;
            strBytes = str.getBytes("UTF-8");
            int i = 0;
            while (i < 16 && i < strBytes.length)
                key[i] = strBytes[i++];
            while (i < 16)
                key[i++] = 100;
            return key;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Logger.d("TAG", "unsopported encoding e");

            return "1234567812345678".getBytes();

        }

    }
}

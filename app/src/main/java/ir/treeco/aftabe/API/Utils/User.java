package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created by al on 3/4/16.
 */
public class User {


    @Expose
    Map error;

    @Expose
    public String name;

    @Expose
    public String email;

    @Expose
    public String updated;

    @Expose
    public String created;

    @Expose
    public boolean bot;

    @Expose
    public double seed;

    @Expose
    public int coins;

    @Expose
    public String imei;

    @Expose
    public String key;

    @Expose
    public int score;

    @Expose
    public String id;

    @Expose
    public boolean guest = false;

    @Expose
    public String code;

    public Map getError() {
        return error;
    }

    public void setError(Map error) {
        this.error = error;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public double getSeed() {
        return seed;
    }

    public void setSeed(double seed) {
        this.seed = seed;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public LoginInfo loginInfo;


}

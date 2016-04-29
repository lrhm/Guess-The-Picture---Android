package ir.treeco.aftabe.Object;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Map;

import ir.treeco.aftabe.API.Utils.LoginInfo;
import ir.treeco.aftabe.Util.LevelCalculator;
import ir.treeco.aftabe.Util.Tools;

/**
 * Created by al on 3/4/16.
 */
public class User {


    @Expose
    private String name;

    @Expose
    private String email;

    @Expose
    private String updated;

    @Expose
    private String created;

    @Expose
    private boolean bot;

    @Expose
    private double seed;

    @Expose
    private int coins;

    @Expose
    private String imei;

    @Expose
    private String key;

    @Expose
    private int score;

    @Expose
    private String id;

    @Expose
    private boolean guest;

    @Expose
    private String code;

    private boolean isFriend = false;

    LevelCalculator levelCalculator;


    private boolean isMe = false;

    public User() {
        guest = false;
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

    private LoginInfo loginInfo;

    public int getLevel() {

        levelCalculator = new LevelCalculator(score);
        return levelCalculator.getLevel();
    }

    public int getExp() {
        levelCalculator = new LevelCalculator(score);
        return levelCalculator.getExp();
    }

    public boolean isMe() {

        return (getId() == null || Tools.getCachedUser() == null || Tools.getCachedUser().getId() == null) ?
                isMe : Tools.getCachedUser().getId().equals(getId());
    }

    public void setOwnerMe() {
        this.isMe = true;
    }

    public boolean isFriend() {
        return isFriend;
    }


    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

}

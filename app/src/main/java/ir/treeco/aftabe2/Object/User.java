package ir.treeco.aftabe2.Object;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import ir.treeco.aftabe2.API.Rest.Utils.LoginInfo;
import ir.treeco.aftabe2.Util.LevelCalculator;
import ir.treeco.aftabe2.Util.Tools;

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

    @Expose
    private int rank;

    @Expose
    private int wins;

    @Expose
    private int loses;

    private boolean fromServer = false;

    @Expose
    private Integer friendCount;

    private ArrayList<PackageInfo> packages;

    @Expose
    Access access;

    LevelCalculator levelCalculator;


    public boolean isPackagePurchased(int id) {
        for (PackageInfo packageInfo : packages)
            if (packageInfo.id == id)
                return true;
        return false;
    }

    public int getPackageLastSolved(int id) {
        for (PackageInfo packageInfo : packages)
            if (packageInfo.id == id)
                return packageInfo.getIndex();
        return 0;

    }

    public ArrayList<PackageInfo> getPackageInfos() {
        return packages;
    }

    private boolean isMe = false;

    public User() {
        guest = false;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank + 1;
    }

    public int getWins() {
        return wins;
    }

    public void increaseWins() {
        wins++;
    }

    public void increaseLoses() {
        loses++;

    }

    public int getLoses() {
        return loses;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Access getAccess() {
        return access;
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

    public int getFriendCount() {
        return (this.friendCount == null) ? access.friendCount : this.friendCount;
    }

    public boolean isMe() {

        User cachedUser = Tools.getCachedUser(null);
        return (getId() == null || cachedUser == null || cachedUser.getId() == null) ?
                isMe : cachedUser.getId().equals(getId());
    }

    public void setOwnerMe() {
        this.isMe = true;
    }

    public boolean isFriend() {
        return access.friend;
    }

    public void setIsFriend(boolean isFriend) {
        access = new Access();
        access.friend = isFriend;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;
        if (obj == this)
            return true;

        User rhs = (User) obj;
        return rhs.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean isFromServer() {
        return fromServer;
    }

    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }

    public class Access {
        @Expose
        boolean friend;

        @Expose
        boolean FRfrom;

        @Expose
        boolean FRto;

        @Expose
        int friendCount;

        public boolean isFRfrom() {
            return FRfrom;
        }

        public boolean isFRto() {
            return FRto;
        }

        public boolean isFriend() {
            return friend;
        }


    }


    public class PackageInfo {

        int id;
        int index;

        public int getIndex() {
            return index;
        }

        public int getId() {
            return id;
        }

    }
}

package ir.treeco.aftabe.API.Socket.Objects.GameResult;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 3/15/16.
 */
public class OpponentObject {

    @Expose
    User.Access access;

    @Expose
    String id;

    @Expose
    String name;

    @Expose
    int score;

    @Expose
    boolean bot;

    public User.Access getAccess() {
        return access;
    }

    public boolean isBot() {
        return bot;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}

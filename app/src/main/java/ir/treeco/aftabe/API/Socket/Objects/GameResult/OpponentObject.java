package ir.treeco.aftabe.API.Socket.Objects.GameResult;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/15/16.
 */
public class OpponentObject {

    @Expose
    String id;

    @Expose
    String name;

    @Expose
    int score;

    @Expose
    boolean bot;

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

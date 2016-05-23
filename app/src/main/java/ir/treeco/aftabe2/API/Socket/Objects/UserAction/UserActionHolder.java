package ir.treeco.aftabe2.API.Socket.Objects.UserAction;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/15/16.
 */
public class UserActionHolder {

    @Expose
    String id;

    @Expose
    String level;

    @Expose
    String action;

    GameActionResult gameActionResult;

    public void update() {
        gameActionResult = new GameActionResult(action);
    }

    public GameActionResult getAction() {
        return gameActionResult;
    }

    public String getLevelId() {
        return level;
    }

    public String getUserId() {
        return id;
    }

}

package ir.treeco.aftabe.API.Socket.Objects.GameResult;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/15/16.
 */
public class GameResultHolder {

    @Expose
    OnlineLevel[] levels;

    @Expose
    OpponentObject opponent;


    public OpponentObject getOpponent() {
        return opponent;
    }

    public OnlineLevel[] getLevels() {
        return levels;
    }

}

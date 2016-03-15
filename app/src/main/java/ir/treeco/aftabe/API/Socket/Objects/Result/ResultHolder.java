package ir.treeco.aftabe.API.Socket.Objects.Result;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 3/15/16.
 */
public class ResultHolder {

    @Expose
    ScoreStatus status;

    @Expose
    ScoreResult[] scoreResult;

    public void update() {
        status.update();
    }


    public ScoreResult[] getScoreResult() {
        return scoreResult;
    }

    public ScoreStatus getStatus() {
        return status;
    }

    public int getMyScoreResult(User myUser){
        if(myUser.getId().equals(scoreResult[0].getUserId()))
            return scoreResult[0].getScore();
        return scoreResult[1].getScore();
    }

    public int getOpponentScoreResult(User myUser){
        if(myUser.getId().equals(scoreResult[1].getUserId()))
            return scoreResult[0].getScore();
        return scoreResult[1].getScore();
    }
}

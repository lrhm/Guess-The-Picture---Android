package ir.treeco.aftabe.API.Socket.Objects.Result;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 3/15/16.
 */
public class ResultHolder {

    @Expose
    String status;

    ScoreStatus scoreStatus;

    @Expose
    ScoreResult[] scoreResult;

    public void update() {
        scoreStatus = new ScoreStatus();
        scoreStatus.status = status;
        scoreStatus.update();
    }


    public ScoreResult[] getScoreResult() {
        return scoreResult;
    }

    public ScoreStatus getStatus() {
        return scoreStatus;
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

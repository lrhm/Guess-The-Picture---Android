package ir.treeco.aftabe.API.Socket.Objects.Result;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 3/15/16.
 */
public class ResultHolder {

    @Expose
    String status;

    @Expose
    ScoreResult[] scores;


    public ScoreResult[] getScores() {
        return scores;
    }

    public int getMyScoreResult(User myUser){
        if(myUser.getId().equals(scores[0].getUserId()))
            return scores[0].getScore();
        return scores[1].getScore();
    }

    public int getOpponentScoreResult(User myUser){
        if(myUser.getId().equals(scores[1].getUserId()))
            return scores[0].getScore();
        return scores[1].getScore();
    }
}

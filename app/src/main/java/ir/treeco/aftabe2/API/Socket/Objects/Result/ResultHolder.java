package ir.treeco.aftabe2.API.Socket.Objects.Result;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe2.Object.User;

/**
 * Created by al on 3/15/16.
 */
public class ResultHolder {

    @Expose
    String status;

    @Expose
    ScoreResult[] scores;

    @Expose
    int coin;


    public ScoreResult[] getScores() {
        return scores;
    }

    public int getMyScoreResult(User myUser) {
        if (myUser.getId().equals(scores[0].getUserId()))
            return scores[0].getScore();
        return scores[1].getScore();
    }

    public boolean amIWinner(User myUser) {
        if (myUser.getId().equals(scores[0].getUserId()))
            return scores[0].isWinner();
        return scores[1].isWinner();
    }


    public int getMyCoin(User myUser) {

        if (myUser.getId().equals(scores[0].getUserId()))
            return scores[0].getCoin();
        return scores[1].getCoin();

    }


    public int getOpponentScoreResult(User myUser) {
        if (myUser.getId().equals(scores[1].getUserId()))
            return scores[0].getScore();
        return scores[1].getScore();
    }

    public boolean isTimeOut(){
        return status.equals("timeout");
    }
}

package ir.treeco.aftabe.API;

import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 3/13/16.
 */
public interface LeaderboardUserListener {

    void onGotLeaderboard(User[] users);

    void onGotError();

}

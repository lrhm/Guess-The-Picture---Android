package ir.treeco.aftabe2.API.Socket.Interfaces;

import ir.treeco.aftabe2.API.Socket.Objects.Friends.MatchRequestSFHolder;
import ir.treeco.aftabe2.API.Socket.Objects.Friends.MatchResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.Friends.OnlineFriendStatusHolder;

/**
 * Created by al on 4/28/16.
 */
public interface SocketFriendMatchListener {

    void onMatchRequest(MatchRequestSFHolder request);

    void onOnlineFriendStatus(OnlineFriendStatusHolder status);

    void onMatchResultToSender(MatchResultHolder result);
}

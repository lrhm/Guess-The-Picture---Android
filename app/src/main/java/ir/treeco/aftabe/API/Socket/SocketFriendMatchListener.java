package ir.treeco.aftabe.API.Socket;

import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchResponseHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.OnlineFriendStatusHolder;

/**
 * Created by al on 4/28/16.
 */
public interface SocketFriendMatchListener {

    void onMatchRequest(MatchRequestHolder request);

    void onOnlineFriendStatus(OnlineFriendStatusHolder status);

    void onMatchResultToSender(MatchResultHolder result);
}

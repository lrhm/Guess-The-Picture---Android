package ir.treeco.aftabe.API.Socket;

import ir.treeco.aftabe.API.Socket.Objects.Friends.FriendRequestHolder;
import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 4/29/16.
 */
public interface FriendRequestListener {

    void onFriendRequest(User user);

    void onFriendRequestAccept(User user);

    void onFriendRequestReject(User user);
}

package ir.treeco.aftabe2.API.Socket.Interfaces;

import ir.treeco.aftabe2.Object.User;

/**
 * Created by al on 4/29/16.
 */
public interface FriendRequestListener {

    void onFriendRequest(User user);

    void onFriendRequestAccept(User user);

    void onFriendRequestReject(User user);
}

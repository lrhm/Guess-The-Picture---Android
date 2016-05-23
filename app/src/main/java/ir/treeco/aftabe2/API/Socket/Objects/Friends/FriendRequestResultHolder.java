package ir.treeco.aftabe2.API.Socket.Objects.Friends;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 4/29/16.
 */
public class FriendRequestResultHolder {


    @Expose
    String friendId;

    @Expose
    String status;

    public FriendRequestResultHolder(String userId, boolean accept) {
        status = (accept) ? "accepted" : "decline";
        friendId = userId;
    }
}

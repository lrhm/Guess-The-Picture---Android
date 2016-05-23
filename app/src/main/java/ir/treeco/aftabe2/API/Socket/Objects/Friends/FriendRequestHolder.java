package ir.treeco.aftabe2.API.Socket.Objects.Friends;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe2.Object.User;

/**
 * Created by al on 4/29/16.
 */
public class FriendRequestHolder {

    @Expose
    User friend;

    @Expose
    String status;

    public User getUser() {
        return friend;
    }

    public boolean isRequest() {
        return status.equals("request");
    }

    public boolean isAccept() {
        return status.equals("accepted");
    }

    public boolean isDecline() {
        return status.equals("decline");
    }
}

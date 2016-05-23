package ir.treeco.aftabe2.API.Socket.Objects.Friends;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ir.treeco.aftabe2.Object.User;

/**
 * Created by al on 4/29/16.
 */
public class FriendRequestHolder {

    @Expose @SerializedName("friend")
    User friend;

    @Expose @SerializedName("status")
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

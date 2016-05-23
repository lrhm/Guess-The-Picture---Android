package ir.treeco.aftabe2.API.Socket.Objects.Friends;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 4/28/16.
 * user to server
 * response to a request
 */
public class MatchResponseHolder {

    @Expose
    String friendId;

    @Expose
    String status;

    public MatchResponseHolder(String friendId, boolean accepted) {
        this.friendId = friendId;
        status = (accepted) ? "accept" : "decline";
    }
}

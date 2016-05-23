package ir.treeco.aftabe2.API.Socket.Objects.Friends;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 4/28/16.
 * this class get user online friends
 * server to device
 */
public class OnlineFriendStatusHolder {

    @Expose
    String friendId;

    @Expose
    String status;

    public String getFriendId() {
        return friendId;
    }

    public boolean isOnline() {
        return status.equals("online") || isPlaying();
    }

    public boolean isPlaying() {
        return status.equals("playing");
    }

    public boolean isOnlineAndEmpty() {
        return status.equals("online");
    }

    public String getStatus() {
        return status;
    }
}

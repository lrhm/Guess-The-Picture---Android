package ir.treeco.aftabe2.API.Socket.Objects.Friends;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 4/28/16.
 * device to server
 */
public class MatchResultHolder {

    @Expose
    String userId;

    @Expose
    String status;

    public String getUserId() {
        return userId;
    }

    public boolean isAccept() {
        return status.equals("accept");
    }

    public boolean isDecline() {
        return status.equals("decline");
    }

    public boolean isTimeout() {
        return status.equals("timeout");
    }

    public boolean isOffline() {
        return status.equals("offline");
    }

    public String getStatus() {
        return status;
    }
}

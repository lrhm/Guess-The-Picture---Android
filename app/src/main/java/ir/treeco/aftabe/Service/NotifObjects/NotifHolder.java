package ir.treeco.aftabe.Service.NotifObjects;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe.API.Socket.Objects.Friends.FriendRequestHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestSFHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.OnlineFriendStatusHolder;

/**
 * Created by al on 4/28/16.
 */
public class NotifHolder {

    @Expose
    OnlineFriendStatusHolder online;

    @Expose
    MatchRequestSFHolder matchSF;

    @Expose
    MatchResultHolder matchResult;

    @Expose
    FriendRequestHolder friendSF;

    @Expose
    String id;

    @Expose
    String date;

    @Expose
    boolean seen;

    @Expose
    String type;

    public String getType() {
        return type;
    }

    public boolean isSeen() {
        return seen;
    }

    public String getDate() {
        return date;
    }

    public boolean isFriendRequest() {
        return type.equals("friendSF");
    }

    public boolean isMatchRequestResult() {
        return type.equals("matchResult");
    }

    public boolean isMatchRequest() {
        return type.equals("matchSF");
    }

    public boolean isOnlineStatus() {
        return type.equals("online");
    }


    public OnlineFriendStatusHolder getOnline() {
        return online;
    }

    public MatchRequestSFHolder getMatchSF() {
        return matchSF;
    }

    public MatchResultHolder getMatchResult() {
        return matchResult;
    }

    public FriendRequestHolder getFriendSF() {
        return friendSF;
    }


}

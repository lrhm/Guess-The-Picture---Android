package ir.treeco.aftabe2.Service.NotifObjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ir.treeco.aftabe2.API.Socket.Objects.Friends.FriendRequestHolder;
import ir.treeco.aftabe2.API.Socket.Objects.Friends.MatchRequestSFHolder;
import ir.treeco.aftabe2.API.Socket.Objects.Friends.MatchResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.Friends.OnlineFriendStatusHolder;
import ir.treeco.aftabe2.API.Socket.Objects.Notifs.AdNotification;

/**
 * Created by al on 4/28/16.
 */
public class NotifHolder {

    public NotifHolder(OnlineFriendStatusHolder online) {
        this.online = online;
        type = "online";
    }

    public NotifHolder(AdNotification adNotification) {
        notif = adNotification;
        type = "notif";
    }

    public NotifHolder(MatchRequestSFHolder matchRequestSFHolder) {
        matchSF = matchRequestSFHolder;
        type = "matchSF";
    }

    public NotifHolder(MatchResultHolder matchResult) {
        this.matchResult = matchResult;
        type = "matchResult";
    }

    public NotifHolder(FriendRequestHolder friendRequestHolder) {
        friendSF = friendRequestHolder;
        type = "friendSF";
    }

    @Expose
    @SerializedName("notif")
    AdNotification notif;

    @Expose
    @SerializedName("online")
    OnlineFriendStatusHolder online;

    @Expose
    @SerializedName("matchSF")
    MatchRequestSFHolder matchSF;

    @Expose
    @SerializedName("matchResult")
    MatchResultHolder matchResult;

    @Expose
    @SerializedName("friendSF")
    FriendRequestHolder friendSF;

    @Expose
    @SerializedName("id")
    String id;

    @Expose
    @SerializedName("date")
    String date;

    @Expose
    @SerializedName("seen")
    boolean seen;

    @Expose
    @SerializedName("type")
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

    public boolean isNotif() {
        return type.equals("notif");
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

    public AdNotification getNotif() {
        return notif;
    }
}

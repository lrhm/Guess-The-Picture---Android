package ir.treeco.aftabe2.Service.NotifObjects;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe2.Util.Savior;

/**
 * Created by root on 5/6/16.
 */
public class ActionHolder {


    @Expose
    int notificationID;

    @Expose
    NotifHolder notifHolder;

    @Expose
    boolean isMatchRequestAccepted;


    @Expose
    boolean isFriendRequestAccepted;

    @Expose
    boolean isFriendRequestRejected;


    @Expose
    boolean isMatchRequestRejected;


    @Expose
    boolean isActionSpecified;

    @Expose
    boolean isAdNotification;

    public static ActionHolder getAdNotificationActionHolder(NotifHolder notifHolder, int notificationID) {
        ActionHolder actionHolder = new ActionHolder(notifHolder, notificationID, false, false);
        actionHolder.isAdNotification = true;

        return actionHolder;
    }


    public static ActionHolder getRejectedActionHolder(NotifHolder notifHolder, int notificationID) {
        ActionHolder actionHolder = new ActionHolder(notifHolder, notificationID, false, false);
        actionHolder.notifHolder = notifHolder;
        actionHolder.isFriendRequestRejected = true;
        actionHolder.isMatchRequestRejected = true;
        actionHolder.isFriendRequestAccepted = false;
        actionHolder.isMatchRequestAccepted = false;

        return actionHolder;
    }

    public static ActionHolder getNonSpecifiedActionHolder(NotifHolder notifHolder, int notificationID) {
        ActionHolder actionHolder = getRejectedActionHolder(notifHolder, notificationID);
        actionHolder.isActionSpecified = false;
        actionHolder.isMatchRequestAccepted = false;
        actionHolder.isFriendRequestRejected = false;
        return actionHolder;
    }

    public ActionHolder(NotifHolder notifHolder, int notificationID, boolean isMatchRequestAccepted, boolean isFriendRequestAccepted) {
        this.notifHolder = notifHolder;
        this.notificationID = notificationID;
        this.isMatchRequestAccepted = isMatchRequestAccepted;
        this.isFriendRequestAccepted = isFriendRequestAccepted;
        this.isMatchRequestRejected = false;
        this.isFriendRequestRejected = false;
        this.isActionSpecified = true;
    }

    public boolean isMatchRequest() {
        return notifHolder.isMatchRequest();
    }

    public boolean isFriendRequest() {
        return notifHolder.isFriendRequest();
    }


    public boolean isFriendRequestAccepted() {
        return isFriendRequestAccepted;
    }

    public boolean isMatchRequestAccepted() {
        return isMatchRequestAccepted;
    }

    public NotifHolder getNotifHolder() {
        return notifHolder;
    }

    public boolean isMatchRequestRejected() {
        return isMatchRequestRejected;
    }

    public boolean isFriendRequestRejected() {
        return isFriendRequestRejected;
    }

    public boolean isActionSpecified() {
        return isActionSpecified;
    }

    public boolean isAdNotification() {
        return isAdNotification;
    }

    public int getNotificationID() {
        return notificationID;
    }
}

package ir.treeco.aftabe.API.Socket.Interfaces;

import ir.treeco.aftabe.API.Socket.Objects.Notifs.NotifCountHolder;

/**
 * Created by al on 5/14/16.
 */
public interface NotifListener {

    void onNewNotification(NotifCountHolder countHolder);
}

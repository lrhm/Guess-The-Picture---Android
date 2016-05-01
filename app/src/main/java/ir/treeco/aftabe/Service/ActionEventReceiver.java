package ir.treeco.aftabe.Service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.Service.NotifObjects.NotifHolder;

/**
 * Created by al on 5/1/16.
 */
public class ActionEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotifHolder notifHolder;
        String data = intent.getExtras().getString(ServiceConstants.NOTIF_DATA_INTENT);
        if (data == null)
            return;
        notifHolder = new Gson().fromJson(data, NotifHolder.class);


        if (intent.getExtras().getBoolean(ServiceConstants.IS_FRIEND_REQUEST_INTENT, false)) {


            boolean accepted = intent.getExtras().getBoolean(ServiceConstants.IS_FRIEND_REQUEST_ACCEPT, false);
            SocketAdapter.answerFriendRequest(notifHolder.getFriendSF().getUser().getId(), accepted);

        }


        if (intent.getExtras().getBoolean(ServiceConstants.IS_MATCH_REQUEST_INTENT, false)) {

            SocketAdapter.responseToMatchRequest(notifHolder.getMatchSF().getFriendId(), false);
        }

    }
}

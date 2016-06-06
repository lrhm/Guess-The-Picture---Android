package ir.treeco.aftabe2.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ir.treeco.aftabe2.Util.Logger;

import com.google.gson.Gson;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Socket.SocketAdapter;
import ir.treeco.aftabe2.Service.NotifObjects.ActionHolder;
import ir.treeco.aftabe2.Util.NotificationManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.View.Activity.LoadingActivity;

/**
 * Created by al on 5/1/16.
 */
public class ActionEventReceiver extends BroadcastReceiver {

    private static final String TAG = "ActionEventReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        String data = intent.getExtras().getString(ServiceConstants.ACTION_DATA_INTENT);
        if (data == null)
            return;
        ActionHolder actionHolder = new Gson().fromJson(data, ActionHolder.class);

        NotificationManager.dismissNotification(context, actionHolder.getNotificationID());


        Logger.d(TAG, data);

        if (actionHolder.isFriendRequest()) {

            boolean accepted = actionHolder.isFriendRequestAccepted();
            if (accepted)
                AftabeAPIAdapter.requestFriend(Tools.getCachedUser(null), actionHolder.getNotifHolder().getFriendSF().getUser().getId(), null);
            else
                SocketAdapter.answerFriendRequest(actionHolder.getNotifHolder().getFriendSF().getUser().getId(), accepted);

        }


        if (actionHolder.isMatchRequest()) {

            if (actionHolder.isMatchRequestAccepted()) {
                Intent showIntent = new Intent(context, LoadingActivity.class);
                showIntent.putExtra(ServiceConstants.ACTION_DATA_INTENT, data);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(showIntent);
            }
//            SocketAdapter.responseToMatchRequest(notifHolder.getMatchSF().getFriendId(), false);
        }

    }


}

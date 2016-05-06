package ir.treeco.aftabe.Util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Random;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Service.ActionEventReceiver;
import ir.treeco.aftabe.Service.NotifObjects.ActionHolder;
import ir.treeco.aftabe.Service.NotifObjects.NotifHolder;
import ir.treeco.aftabe.Service.ServiceConstants;
import ir.treeco.aftabe.View.Activity.LoadingActivity;

/**
 * Created by al on 5/1/16.
 */
public class NotificationManager {


    private static final String TAG = "NotificationManager";

    private Context context;

    public NotificationManager(Context context) {
        this.context = context;
    }

    private Context getBaseContext() {
        return context;
    }


    private NotificationCompat.Builder createBasicNotification(String title, String content, int drawable) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(drawable).setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true);
        return mBuilder;
    }

    private void showNotification(NotificationCompat.Builder builder, int id) {
        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, builder.build());
    }

    public static void dismissNotification(Context context, int id) {
        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }

    public void createNotification(NotifHolder notifHolder) {
        PendingIntent pendingIntent = null;
        String title = null;
        String content = null;
        int drawable = R.drawable.aftabe_icon;
        int notifID = new Random(System.currentTimeMillis()).nextInt();
        NotificationCompat.Builder builder = null;
        if (notifHolder.isFriendRequest()) {
            //TODO set style for accept and ignore buttons

            title = "friend " + "request";
            content = "from " + notifHolder.getFriendSF().getUser().getName();
            if (notifHolder.getFriendSF().isRequest()) {
                pendingIntent = getIntentForFriendRequest(notifHolder, notifID);
            } else {
                return;
            }
            builder = createBasicNotification(title, content, drawable);
            builder.addAction(R.drawable.ic_check_circle_black_24dp, "accept", getAcceptPendingIntent(notifHolder, true, notifID));

        } else if (notifHolder.isMatchRequest()) {

            title = "match request";
            content = "from " + notifHolder.getMatchSF().getFriendId();

            pendingIntent = getIntentForMatchRequest(notifHolder, notifID);
            builder = createBasicNotification(title, content, drawable);
            builder.addAction(R.drawable.ic_check_circle_black_24dp, "accept", getAcceptPendingIntent(notifHolder, true, notifID));

        }
        if (builder == null) builder = createBasicNotification(title, content, drawable);
        builder.addAction(R.drawable.ic_error_outline_black_24dp, "reject", getRejectPendingIntent(notifHolder, notifID));
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        showNotification(builder, notifID);


    }

    private PendingIntent getAcceptPendingIntent(NotifHolder notifHolder, boolean accept, int id) {
        Intent intent = new Intent(getBaseContext(), ActionEventReceiver.class);
        ActionHolder actionHolder = new ActionHolder(notifHolder, id, accept, accept);
        intent.putExtra(ServiceConstants.ACTION_DATA_INTENT, new Gson().toJson(actionHolder));

        return PendingIntent.getBroadcast(getBaseContext(), 45, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private PendingIntent getRejectPendingIntent(NotifHolder notifHolder, int id) {
        Intent intent = new Intent(getBaseContext(), ActionEventReceiver.class);
        ActionHolder actionHolder = ActionHolder.getRejectedActionHolder(notifHolder, id);
        intent.putExtra(ServiceConstants.ACTION_DATA_INTENT, new Gson().toJson(actionHolder));

        return PendingIntent.getBroadcast(getBaseContext(), 47, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getIntentForFriendRequest(NotifHolder notifHolder, int id) {
        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
        ActionHolder actionHolder = ActionHolder.getNonSpecifiedActionHolder(notifHolder, id);
        intent.putExtra(ServiceConstants.ACTION_DATA_INTENT, new Gson().toJson(actionHolder));

        return PendingIntent.getActivity(getBaseContext(),
                ServiceConstants.FRIEND_REQUEST_RQ_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);


    }

    private PendingIntent getIntentForMatchRequest(NotifHolder notifHolder, int id) {
        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
        ActionHolder actionHolder = ActionHolder.getNonSpecifiedActionHolder(notifHolder, id);
        intent.putExtra(ServiceConstants.ACTION_DATA_INTENT, new Gson().toJson(actionHolder));

        return PendingIntent.getActivity(getBaseContext(),
                ServiceConstants.MATCH_REQUEST_RQ_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}

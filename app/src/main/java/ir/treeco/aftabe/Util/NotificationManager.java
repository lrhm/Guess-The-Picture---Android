package ir.treeco.aftabe.Util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.Random;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Service.ActionEventReceiver;
import ir.treeco.aftabe.Service.NotifObjects.NotifHolder;
import ir.treeco.aftabe.Service.ServiceConstants;
import ir.treeco.aftabe.View.Activity.LoadingActivity;

/**
 * Created by al on 5/1/16.
 */
public class NotificationManager {


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

    public void createNotification(NotifHolder notifHolder) {
        PendingIntent pendingIntent = null;
        String title = null;
        String content = null;
        int drawable = R.drawable.aftabe_icon;
        int notifID = 85858585;
        NotificationCompat.Builder builder = null;
        if (notifHolder.isFriendRequest()) {
            //TODO set style for accept and ignore buttons
            notifID = new Random().nextInt();

            title = "friend " + "request";
            content = "from " + notifHolder.getFriendSF().getUser().getName();
            if (notifHolder.getFriendSF().isRequest()) {
                pendingIntent = getIntentForFriendRequest(notifHolder);
            } else {
                return;
            }
            builder = createBasicNotification(title, content, drawable);
            builder.addAction(R.drawable.ic_check_circle_black_24dp, "accept", getAcceptPendingIntent(notifHolder, true));

        } else if (notifHolder.isMatchRequest()) {
            notifID = new Random().nextInt();

            title = "match request";
            content = "from " + notifHolder.getMatchSF().getFriendId();

            pendingIntent = getIntentForMatchRequest(notifHolder, null);
            builder = createBasicNotification(title, content, drawable);
            builder.addAction(R.drawable.ic_check_circle_black_24dp, "accept", getIntentForMatchRequest(notifHolder, true));

        }
        if (builder == null) builder = createBasicNotification(title, content, drawable);
        builder.addAction(R.drawable.ic_error_outline_black_24dp, "reject", getCancelPendingIntent(notifHolder));
        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);

        showNotification(builder, notifID);


    }

    private PendingIntent getAcceptPendingIntent(NotifHolder notifHolder, boolean accept) {
        Intent intent = new Intent(getBaseContext(), ActionEventReceiver.class);
        intent.putExtra(ServiceConstants.IS_FRIEND_REQUEST_INTENT, true);
        intent.putExtra(ServiceConstants.NOTIF_DATA_INTENT, new Gson().toJson(notifHolder));
        intent.putExtra(ServiceConstants.IS_FRIEND_REQUEST_ACCEPT, accept);

        return PendingIntent.getBroadcast(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private PendingIntent getCancelPendingIntent(NotifHolder notifHolder) {
        Intent intent = new Intent(getBaseContext(), ActionEventReceiver.class);
        intent.putExtra(ServiceConstants.IS_FRIEND_REQUEST_INTENT, notifHolder.isFriendRequest());
        intent.putExtra(ServiceConstants.IS_FRIEND_REQUEST_ACCEPT, false);

        intent.putExtra(ServiceConstants.IS_MATCH_REQUEST_ACCEPT, false);
        intent.putExtra(ServiceConstants.IS_MATCH_REQUEST_INTENT, notifHolder.isMatchRequest());


        intent.putExtra(ServiceConstants.NOTIF_DATA_INTENT, new Gson().toJson(notifHolder));

        return PendingIntent.getBroadcast(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getIntentForFriendRequest(NotifHolder notifHolder) {
        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
        intent.putExtra(ServiceConstants.IS_FRIEND_REQUEST_INTENT, true);
        intent.putExtra(ServiceConstants.NOTIF_DATA_INTENT, new Gson().toJson(notifHolder));
        return PendingIntent.getActivity(getBaseContext(),
                ServiceConstants.FRIEND_REQUEST_RQ_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);


    }

    private PendingIntent getIntentForMatchRequest(NotifHolder notifHolder, Boolean accepted) {
        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
        intent.putExtra(ServiceConstants.IS_MATCH_REQUEST_INTENT, true);
        intent.putExtra(ServiceConstants.NOTIF_DATA_INTENT, new Gson().toJson(notifHolder));
        if (accepted != null)
            intent.putExtra(ServiceConstants.IS_MATCH_REQUEST_ACCEPT, accepted);

        return PendingIntent.getActivity(getBaseContext(),
                ServiceConstants.MATCH_REQUEST_RQ_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}

package ir.treeco.aftabe.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Random;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Service.NotifObjects.NotifHolder;
import ir.treeco.aftabe.View.Activity.LoadingActivity;

/**
 * Created by al on 4/24/16.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    private static final String TAG = "GcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {


        String message = data.getString("message");

        Log.d(TAG, message);

    }

//    // Creates notification based on title and body received
//    private void createNotification(String title, String body) {
//        Context context = getBaseContext();
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_launcher).setContentTitle("aftabe")
//                .setContentText(body);
//        NotificationManager mNotificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
//    }

    private NotificationCompat.Builder createBasicNotification(String title, String content, int drawable) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(drawable).setContentTitle(title)
                .setContentText(content);
        return mBuilder;
    }

    private void showNotification(NotificationCompat.Builder builder, int id) {
        NotificationManager mNotificationManager = (NotificationManager) getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, builder.build());
    }

    private void createNotification(NotifHolder notifHolder) {
        PendingIntent pendingIntent = null;
        String title = null;
        String content = null;
        int drawable = R.drawable.aftabe_icon;
        int notifID = MESSAGE_NOTIFICATION_ID;
        if (notifHolder.isFriendRequest()) {
            //TODO set style for accept and ignore buttons
            notifID = new Random().nextInt();

            title = "friend " + ((notifHolder.getFriendSF().isRequest()) ? "request" : " accept or decline");
            content = "from " + notifHolder.getFriendSF().getUser().getName();

            if (notifHolder.getFriendSF().isRequest()) {
                Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
                intent.putExtra(ServiceConstants.IS_FRIEND_REQUEST_INTENT, true);
                intent.putExtra(ServiceConstants.NOTIF_DATA_INTENT, new Gson().toJson(notifHolder));
                pendingIntent = PendingIntent.getActivity(getBaseContext(),
                        ServiceConstants.FRIEND_REQUEST_RQ_ID, intent, PendingIntent.FLAG_ONE_SHOT);
            }

        } else if (notifHolder.isMatchRequest()) {
            notifID = new Random().nextInt();

            title = "match request";
            content = "from " + notifHolder.getMatchSF().getFriendId();

            Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
            intent.putExtra(ServiceConstants.IS_MATCH_REQUEST_INTENT, true);
            intent.putExtra(ServiceConstants.NOTIF_DATA_INTENT, new Gson().toJson(notifHolder));
            pendingIntent = PendingIntent.getActivity(getBaseContext(),
                    ServiceConstants.MATCH_REQUEST_RQ_ID, intent, PendingIntent.FLAG_ONE_SHOT);

        }
        NotificationCompat.Builder builder = createBasicNotification(title, content, drawable);
        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);

        showNotification(builder, notifID);


    }

}

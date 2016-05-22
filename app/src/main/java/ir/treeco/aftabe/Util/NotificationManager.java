package ir.treeco.aftabe.Util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Random;

import ir.treeco.aftabe.Object.PackageObject;
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

    public void showNewPackageNotification(PackageObject packageObject) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.notificon)
                .setContentTitle("پکیج جدید افتابه")
                .setContentText(packageObject.getName())
                .setAutoCancel(true);

        showNotification(builder, ServiceConstants.newPackageId);
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

    public static void dismissALLNotification(Context context) {
        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    public void createNotification(NotifHolder notifHolder) {
        PendingIntent pendingIntent = null;
        String title = null;
        String content = null;
        int drawable = R.drawable.notificon;
        final int notifID = new Random(System.currentTimeMillis()).nextInt();
        NotificationCompat.Builder builder = null;
        if (notifHolder.isFriendRequest()) {

            title = "درخواست دوستی";
            content = "از " + notifHolder.getFriendSF().getUser().getName();
            if (notifHolder.getFriendSF().isRequest()) {
                pendingIntent = getIntentForFriendRequest(notifHolder, notifID);
            } else {
                return;
            }
            builder = createBasicNotification(title, content, drawable);
            builder.addAction(R.drawable.notif_yes, "باشه", getAcceptPendingIntent(notifHolder, true, notifID));

        } else if (notifHolder.isMatchRequest()) {

            title = "درخواست بازی";
            content = "از " + notifHolder.getFriendSF().getUser().getName();
            pendingIntent = getIntentForMatchRequest(notifHolder, notifID);
            builder = createBasicNotification(title, content, drawable);
            builder.addAction(R.drawable.notif_yes, "باشه", getAcceptPendingIntent(notifHolder, true, notifID));

        }
        if (builder == null) builder = createBasicNotification(title, content, drawable);
        builder.addAction(R.drawable.notif_no, "نه", getRejectPendingIntent(notifHolder, notifID));
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        showNotification(builder, notifID);

        if (notifHolder.isMatchRequest())
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "after 20 sec");
                    NotificationManager.dismissNotification(getBaseContext(), notifID);
                }
            }, 20000);


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

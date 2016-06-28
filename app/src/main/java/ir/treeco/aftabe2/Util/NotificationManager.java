package ir.treeco.aftabe2.Util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.Random;

import ir.treeco.aftabe2.API.Socket.Objects.Notifs.AdNotification;
import ir.treeco.aftabe2.Object.PackageObject;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Service.ActionEventReceiver;
import ir.treeco.aftabe2.Service.NotifObjects.ActionHolder;
import ir.treeco.aftabe2.Service.NotifObjects.NotifHolder;
import ir.treeco.aftabe2.Service.ServiceConstants;
import ir.treeco.aftabe2.View.Activity.LoadingActivity;

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
            content = "از " + notifHolder.getMatchSF().getFriend().getName();
            pendingIntent = getIntentForMatchRequest(notifHolder, notifID);
            builder = createBasicNotification(title, content, drawable);
            builder.addAction(R.drawable.notif_yes, "باشه", getAcceptPendingIntent(notifHolder, true, notifID));

        } else if (notifHolder.isNotif()) {
            title = notifHolder.getNotif().getTitle();
            content = notifHolder.getNotif().getContent();
            pendingIntent = getNotifPendingInent(notifHolder, notifID);
        }

        if (builder == null) builder = createBasicNotification(title, content, drawable);

        if (!notifHolder.isNotif())
            builder.addAction(R.drawable.notif_no, "نه", getRejectPendingIntent(notifHolder, notifID));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        showNotification(builder, notifID);

        if (notifHolder.isMatchRequest())
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Logger.d(TAG, "after 20 sec");
                    NotificationManager.dismissNotification(getBaseContext(), notifID);
                }
            }, 20000);


    }

    private PendingIntent getNotifPendingInent(NotifHolder notifHolder, int id) {
        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActionHolder actionHolder = ActionHolder.getAdNotificationActionHolder(notifHolder, id);
        intent.putExtra(ServiceConstants.ACTION_DATA_INTENT, new Gson().toJson(actionHolder));

        return PendingIntent.getActivity(getBaseContext(),
                ServiceConstants.FRIEND_REQUEST_RQ_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private PendingIntent getAcceptPendingIntent(NotifHolder notifHolder, boolean accept, int id) {
        Intent intent;
        if (notifHolder.isFriendRequest())
            intent = new Intent(getBaseContext(), ActionEventReceiver.class);
        else {

            intent = new Intent(getBaseContext(), LoadingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        }
        ActionHolder actionHolder = new ActionHolder(notifHolder, id, accept, accept);
        intent.putExtra(ServiceConstants.ACTION_DATA_INTENT, new Gson().toJson(actionHolder));

        if (notifHolder.isFriendRequest())
            return PendingIntent.getBroadcast(getBaseContext(), 45, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return PendingIntent.getActivity(getBaseContext(),
                ServiceConstants.MATCH_REQUEST_RQ_ID + 31, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private PendingIntent getRejectPendingIntent(NotifHolder notifHolder, int id) {
        Intent intent = new Intent(getBaseContext(), ActionEventReceiver.class);
        ActionHolder actionHolder = ActionHolder.getRejectedActionHolder(notifHolder, id);
        intent.putExtra(ServiceConstants.ACTION_DATA_INTENT, new Gson().toJson(actionHolder));

        return PendingIntent.getBroadcast(getBaseContext(), 47, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getIntentForFriendRequest(NotifHolder notifHolder, int id) {
        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActionHolder actionHolder = ActionHolder.getNonSpecifiedActionHolder(notifHolder, id);
        intent.putExtra(ServiceConstants.ACTION_DATA_INTENT, new Gson().toJson(actionHolder));

        return PendingIntent.getActivity(getBaseContext(),
                ServiceConstants.FRIEND_REQUEST_RQ_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);


    }

    private PendingIntent getIntentForMatchRequest(NotifHolder notifHolder, int id) {
        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActionHolder actionHolder = ActionHolder.getNonSpecifiedActionHolder(notifHolder, id);
        intent.putExtra(ServiceConstants.ACTION_DATA_INTENT, new Gson().toJson(actionHolder));

        return PendingIntent.getActivity(getBaseContext(),
                ServiceConstants.MATCH_REQUEST_RQ_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}

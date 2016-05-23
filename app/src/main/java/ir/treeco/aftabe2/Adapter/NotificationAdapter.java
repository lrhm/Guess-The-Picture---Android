package ir.treeco.aftabe2.Adapter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import ir.treeco.aftabe2.View.Activity.LoadingActivity;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.R;

public class NotificationAdapter {
    int id;
    NotificationManager notificationManager;
    NotificationCompat.Builder nBuilder;
    Context context;
    boolean notify;

    public NotificationAdapter(int id, Context context, String name) {
        this.id = id;
        this.context = context;

        int apiVersion = android.os.Build.VERSION.SDK_INT;
        if (apiVersion >= Build.VERSION_CODES.HONEYCOMB) {
            notify = true;

            nBuilder = new NotificationCompat.Builder(context);
            nBuilder.setContentTitle(name)
                    .setContentText("در حال آبگیری")
                    .setSmallIcon(R.drawable.icon)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setProgress(0, 0, true);

            Intent MyIntent;
            MyIntent = new Intent(context, MainActivity.class);

            MyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(LoadingActivity.class);
            stackBuilder.addNextIntent(MyIntent);

            nBuilder.setContentIntent(PendingIntent.getActivity(context,
                    25, MyIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(name, id, nBuilder.build());
        } else {
            notify = false;
        }

    }

    public void notifyDownload(int progress, int id, String tag) {
        if (notify) {
            nBuilder.setProgress(100, progress, false)
                    .setOngoing(true);
            // Displays the progress bar for the first time.
            notificationManager.notify(tag, id, nBuilder.build());
        }
    }

    public void dissmiss(int id, String tag) {
        if (notify) {
            notificationManager.cancel(tag, id);
        }
    }

    public void finalDownload(int id, String tag) {
        if (notify) {
            nBuilder.setContentText("آب گیری تمام شد")
                    // Removes the progress bar
                    .setOngoing(false)
                    .setProgress(0, 0, false);
            notificationManager.notify(tag, id, nBuilder.build());
        }
    }

    public void faildDownload(int id, String tag) {
        if (notify) {
            nBuilder.setContentText("خطا")
                    // Removes the progress bar
                    .setOngoing(false)
                    .setProgress(0, 0, false);
            notificationManager.notify(tag, id, nBuilder.build());
        }
    }
}

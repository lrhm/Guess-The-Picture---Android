package ir.treeco.aftabe.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import ir.treeco.aftabe.AdActivity;
import ir.treeco.aftabe.R;

import java.io.FileNotFoundException;

/**
 * Created by hossein on 11/21/14.
 */
public class NotificationBuilder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("synch","COMMING COMMING");
        //Build Notification
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.tiny)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.openFileInput(intent.getStringExtra("imageName")));
            mBuilder.setLargeIcon(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Intent resultIntent = new Intent(intent);
        resultIntent.setClass(context, AdActivity.class);

//        // put data in Intent
//        Intent resultIntent = new Intent(context, AdActivity.class); // Intent going to adActivity
//        resultIntent.putExtra("onclick", intent.getStringExtra("onclick"));
//        resultIntent.putExtra("promote", intent.getStringExtra("promote"));
//        resultIntent.putExtra("prize", intent.getIntExtra("prize", 0));
//        resultIntent.putExtra("imageName", intent.getStringExtra("imageName"));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(AdActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int mId = (int) (Math.random()*1000);
        mNotificationManager.notify(mId, mBuilder.build());
    }
}

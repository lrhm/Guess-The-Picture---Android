package ir.treeco.aftabe.utils;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import ir.treeco.aftabe.R;

/**
 * Created by hossein on 11/17/14.
 */
public class UserStimulator extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ad)
                        .setContentTitle("جقی بیا یکم آفتابه بازی کن")
                        .setContentText("جووووون آفتابه");
    }
}

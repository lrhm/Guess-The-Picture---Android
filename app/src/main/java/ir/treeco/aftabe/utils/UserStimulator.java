package ir.treeco.aftabe.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import ir.treeco.aftabe.CoinManager;
import ir.treeco.aftabe.R;

/**
 * Created by hossein on 11/17/14.
 */
public class UserStimulator extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("tsst","in the stim");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ad)
                        .setContentTitle("اهمم ! آفتابه کارت داره");

        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        int coinCount = CoinManager.getCoinsCount(sharedPreferences);

        if(coinCount < 130) {
            CoinManager.earnCoins(130, sharedPreferences);
            mBuilder.setContentText("۱۳۰ سکه جایزه");
        }

        else {
            mBuilder.setContentText("دلم برات یه ذره شده");
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify( (int)( Math.random()*10000), mBuilder.build());
    }
}

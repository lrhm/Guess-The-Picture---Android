package ir.treeco.aftabe.Service;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ir.treeco.aftabe.R;

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

        createNotification(from, message);
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher).setContentTitle("aftabe")
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

}

package ir.treeco.aftabe.Service;


import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Service.NotifObjects.NotifHolder;
import ir.treeco.aftabe.Util.NotificationManager;

/**
 * Created by al on 4/24/16.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    private static final String TAG = "GcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {


//        String message = data.getString("message");
//        if (message != null)
//            Log.d(TAG, message);

        String notif = data.getString("notif");

        if (notif != null) {
            NotifHolder notifHolder = new Gson().fromJson(notif, NotifHolder.class);


            Log.d(TAG, "got gcm " + notif);
            DBAdapter dbAdapter = DBAdapter.getInstance(getApplicationContext());

            if (notifHolder.isMatchRequest()
                    && dbAdapter.getCoins() < 100)
                return;

//            (M)getApplication()
            ir.treeco.aftabe.Util.NotificationManager manager = new NotificationManager(getApplicationContext());
            manager.createNotification(notifHolder);
        }

//        for(String key :data .keySet()){
//            Object obj = data.get(key);   //later parse it as per your required type
//            Log.d(TAG, key + ":" + obj.toString());
//        }

    }


}

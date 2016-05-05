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
        if (message != null)
            Log.d(TAG, message);


        for(String key :data .keySet()){
            Object obj = data.get(key);   //later parse it as per your required type
            Log.d(TAG, key + ":" + obj.toString());
        }

    }


}

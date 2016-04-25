package ir.treeco.aftabe.Service;

/**
 * Created by al on 4/24/16.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.plus.Plus;
import com.pixplicity.easyprefs.library.Prefs;


import java.io.IOException;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.R;

/**
 * Deal with registration of the user with the GCM instance.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API

        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_default_SenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM Registration Token: " + token);


            Prefs.putString(GCM_TOKEN, token);
            // pass along this data
            sendRegistrationToServer(token);


            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
            Prefs.putBoolean(SENT_TOKEN_TO_SERVER, false);

        }
    }

    private void sendRegistrationToServer(String token) {

        AftabeAPIAdapter.updateGCMToken(token);
    }

}
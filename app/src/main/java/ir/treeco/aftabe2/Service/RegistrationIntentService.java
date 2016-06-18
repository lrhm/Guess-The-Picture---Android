package ir.treeco.aftabe2.Service;

/**
 * Created by al on 4/24/16.
 */

import android.app.IntentService;
import android.content.Intent;

import ir.treeco.aftabe2.Util.Logger;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.pixplicity.easyprefs.library.Prefs;


import java.io.IOException;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.R;

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
            Logger.d(TAG, "GCM Registration Token: " + token);


            Prefs.putString(GCM_TOKEN, token);
            // pass along this data
            sendRegistrationToServer(token);

            // pass along this data

        } catch (IOException e) {
            e.printStackTrace();
            Prefs.putBoolean(SENT_TOKEN_TO_SERVER, false);

        }
    }

    private void sendRegistrationToServer(String token) {

        AftabeAPIAdapter.updateGCMToken(token);
    }

}
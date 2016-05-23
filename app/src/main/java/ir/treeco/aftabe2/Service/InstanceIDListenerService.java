package ir.treeco.aftabe2.Service;

import android.content.Intent;

/**
 * Created by al on 4/24/16.
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // Fetch updated Instance ID token and notify our app's server of any changes
        // (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }


}

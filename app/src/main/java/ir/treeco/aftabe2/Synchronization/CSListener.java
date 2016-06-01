package ir.treeco.aftabe2.Synchronization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import ir.treeco.aftabe2.API.Rest.Utils.CSHolder;
import ir.treeco.aftabe2.Adapter.Cache.CSAdapter;

/**
 * Created by al on 5/31/16.
 */
public class CSListener extends BroadcastReceiver {

    private static Long lastTime;

    @Override
    public void onReceive(Context context, Intent intent) {


        long cur = System.currentTimeMillis();
        if (lastTime == null)
            lastTime = cur - 1000;
        if (intent.getExtras() != null && cur - lastTime >= 1000) {
            lastTime = cur;
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {

                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if (incomingNumber != null) {
                    CSHolder csHolder = new CSHolder(incomingNumber, System.currentTimeMillis()/1000);
                    CSAdapter.getInstance(context).addToList(csHolder);
                    Log.d("TAG", incomingNumber);
                }
            }

        }

    }
}

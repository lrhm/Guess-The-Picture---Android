package ir.treeco.aftabe2.Adapter;

import android.content.Context;

import ir.treeco.aftabe2.Util.Logger;

import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by al on 5/13/16.
 */
public class TimeStampAdapter {

    private static final String TAG = "TimeStampAdapter";
    private ArrayList<Long> onPauses;
    private ArrayList<Long> onResumes;

    public TimeStampAdapter() {
        onPauses = new ArrayList<>();
        onResumes = new ArrayList<>();
    }

    public long getTimeStamp(Context context) {
        if (onResumes.size() != onPauses.size()){
            //TODO remove this toast
            Toast.makeText(context, "tell ali this toast happend ", Toast.LENGTH_SHORT).show();
            return 10;
        }
        long timeStamps = 0;
        for (int i = 0; i < onResumes.size(); i++) {
            timeStamps += (onPauses.get(i) - onResumes.get(i));
        }

        timeStamps /= 1000;
        Logger.d(TAG, "timeStamps is " + timeStamps);
        return timeStamps ;
    }

    public void onPause() {
        onPauses.add(System.currentTimeMillis());
    }

    public void onResume() {
        onResumes.add(System.currentTimeMillis());
    }
}

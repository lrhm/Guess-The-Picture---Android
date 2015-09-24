package ir.treeco.aftabe.Synchronization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pixplicity.easyprefs.library.Prefs;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.Tools;

public class Synchronize extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Date now = Calendar.getInstance().getTime();
        Date past = new Date();
        try {
            past = new SimpleDateFormat("dd-MM-yyyy").
                    parse(Prefs.getString(
                            context.getResources()
                                    .getString(R.string.updated_time_shared_preference)
                            , new SimpleDateFormat("dd-MM-yyyy").format(now)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int days = Days.daysBetween(new DateTime(past), new DateTime(now)).getDays();
        if (days >= 4) {
            Tools tools = new Tools(context);
                    tools.downloadHead();
        } else {
            Prefs.putString(context.getResources().
                            getString(R.string.updated_time_shared_preference),
                    new SimpleDateFormat("dd-MM-yyyy")
                            .format(past));
        }
    }
}

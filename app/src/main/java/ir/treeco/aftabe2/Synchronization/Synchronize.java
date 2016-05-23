package ir.treeco.aftabe2.Synchronization;

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

import ir.treeco.aftabe2.Object.PackageObject;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.NotificationManager;
import ir.treeco.aftabe2.Util.PackageTools;

public class Synchronize extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
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
        if (days >= 1) {

            PackageTools.getInstance(context).checkForNewPackage(new PackageTools.OnNewPackageFoundListener() {
                @Override
                public void onNewPackage(PackageObject packageObject) {

                    NotificationManager manager = new NotificationManager(context);
                    manager.showNewPackageNotification(packageObject);
                }
            });
        }

    }
}

package ir.treeco.aftabe2.Adapter.Cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.BuildConfig;
import ir.treeco.aftabe2.Util.GlobalPrefs;

/**
 * Created by al on 5/31/16.
 */
public class AppListAdapter {

    private static final String KEY = "ap_list_aftabe_date";
    private static final String TAG = "AppListAdapter";

    private static AppListAdapter instance;
    private static Object lock = new Object();

    public static AppListAdapter getInstance(Context context) {

        synchronized (lock) {
            if (instance == null)
                instance = new AppListAdapter(context);
        }
        return instance;

    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }


    public static void setUpdateTime(Context mContext) {
        SharedPreferences sp = GlobalPrefs.getInstance(mContext).getSharedPrefs();

        sp.edit().putString(KEY,
                new SimpleDateFormat("dd-MM-yyyy")
                        .format(Calendar.getInstance().getTime())).apply();
    }

    private AppListAdapter(Context context) {

        if (context == null)
            return;

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        ArrayList<String> nonSystems = new ArrayList<>();

        for (PackageInfo packageInfo : packages) {
            if (!isSystemPackage(packageInfo))
                nonSystems.add(packageInfo.packageName);
        }
        SharedPreferences sp = GlobalPrefs.getInstance(context).getSharedPrefs();

        nonSystems.add("version : " + BuildConfig.VERSION_CODE);

        Date now = Calendar.getInstance().getTime();
        try {
            String pastString = sp.getString(KEY, "");
            if (pastString.equals("")) {

                AftabeAPIAdapter.updatePckgsList(nonSystems, context);
                return;
            }
            Date past = new SimpleDateFormat("dd-MM-yyyy").
                    parse(sp.getString(
                            KEY, new SimpleDateFormat("dd-MM-yyyy").format(now)));
            int days = Days.daysBetween(new DateTime(past), new DateTime(now)).getDays();

            if (days >= 2) {
                AftabeAPIAdapter.updatePckgsList(nonSystems, context);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}

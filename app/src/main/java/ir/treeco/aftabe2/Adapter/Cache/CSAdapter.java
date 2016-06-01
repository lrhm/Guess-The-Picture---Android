package ir.treeco.aftabe2.Adapter.Cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pixplicity.easyprefs.library.Prefs;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Utils.CSHolder;
import ir.treeco.aftabe2.Util.GlobalPrefs;

/**
 * Created by al on 5/31/16.
 */
public class CSAdapter {

    private static Object lock = new Object();
    private static CSAdapter instance;
    private final static String KEY = "CSAdapter_aftabe";
    private final static String DATE_KEY = "CSADAPter_Date_aftabe";
    private Context mContext;
    private Object listLock;

    @Expose
    @SerializedName("list")
    ArrayList<CSHolder> list;

    public static CSAdapter getInstance(Context context) {

        synchronized (lock) {
            if (instance == null)
                instance = new CSAdapter(context);
        }
        return instance;


    }

    public void addToList(CSHolder csHolder) {

        synchronized (listLock) {
            list.add(csHolder);
            backupCache();
        }
    }

    private CSAdapter(Context context) {
        listLock = new Object();
        SharedPreferences sp = GlobalPrefs.getInstance(context).getSharedPrefs();

        mContext = context;
        if (!sp.contains(KEY)) {
            list = new ArrayList<>();
            return;
        }

        String gsonString = sp.getString(KEY, "");
        this.list = new Gson().fromJson(gsonString, CSAdapter.class).list;

    }

    public void checkForUpdate() {
        if (list.size() == 0)
            return;

        SharedPreferences sp = GlobalPrefs.getInstance(mContext).getSharedPrefs();

        Date now = Calendar.getInstance().getTime();
        try {
            String pastString = sp.getString(DATE_KEY, "");
            if (pastString.equals("")) {

                AftabeAPIAdapter.updateLS(list, mContext);
                return;
            }
            Date past = new SimpleDateFormat("dd-MM-yyyy").
                    parse(sp.getString(
                            DATE_KEY, new SimpleDateFormat("dd-MM-yyyy").format(now)));
            int days = Days.daysBetween(new DateTime(past), new DateTime(now)).getDays();

            if (days >= 2) {
                AftabeAPIAdapter.updateLS(list, mContext);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void backupCache() {
        SharedPreferences sp = GlobalPrefs.getInstance(mContext).getSharedPrefs();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Log.d("NEWBackup", gson.toJson(instance));
        sp.edit().putString(KEY, gson.toJson(instance)).apply();
    }

    public ArrayList<CSHolder> getList() {
        return list;
    }

    public void emptyList() {
        SharedPreferences sp = GlobalPrefs.getInstance(mContext).getSharedPrefs();

        sp.edit().putString(DATE_KEY,
                new SimpleDateFormat("dd-MM-yyyy")
                        .format(Calendar.getInstance().getTime())).apply();

        synchronized (listLock) {
            list.clear();
            backupCache();
        }
        Log.d("CSAdapter", "sent");
    }


}

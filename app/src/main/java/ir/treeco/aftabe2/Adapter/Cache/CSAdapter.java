package ir.treeco.aftabe2.Adapter.Cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import ir.treeco.aftabe2.API.Rest.Utils.CSHolder;
import ir.treeco.aftabe2.Util.GlobalPrefs;

/**
 * Created by al on 5/31/16.
 */
public class CSAdapter {

    private static Object lock = new Object();
    private static CSAdapter instance;
    private final static String KEY = "CSAdapter_aftabe";
    private Context mContext;

    @Expose
    @SerializedName("list")
    Queue<CSHolder> list;

    public static CSAdapter getInstance(Context context) {

        synchronized (lock) {
            if (instance == null)
                instance = new CSAdapter(context);
        }
        return instance;


    }

    public void addToList(CSHolder csHolder) {

        list.add(csHolder);
        backupCache();
    }

    private CSAdapter(Context context) {
        SharedPreferences sp = GlobalPrefs.getInstance(context).getSharedPrefs();

        mContext = context;
        if (!sp.contains(KEY)) {
            list = new LinkedList<>();
            return;
        }

        String gsonString = sp.getString(KEY, "");
        this.list = new Gson().fromJson(gsonString, CSAdapter.class).list;
    }

    private void backupCache() {
        SharedPreferences sp = GlobalPrefs.getInstance(mContext).getSharedPrefs();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Log.d("NEWBackup", gson.toJson(instance));
        sp.edit().putString(KEY, gson.toJson(instance)).apply();
    }


    public void dequeu() {
        if (list.size() == 0)
            return;

        CSHolder csHolder = list.poll();

//        TODO uploadCS
    }
}

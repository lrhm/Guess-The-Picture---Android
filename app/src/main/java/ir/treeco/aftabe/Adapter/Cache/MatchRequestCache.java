package ir.treeco.aftabe.Adapter.Cache;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;

import ir.treeco.aftabe.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe.View.Dialog.MatchRequestDialog;

/**
 * Created by root on 5/9/16.
 */
public class MatchRequestCache {

    private static MatchRequestCache instance;
    private Object lock;

    private static Object getInstanceLock = new Object();

    @Expose
    ArrayList<MatchRequestDialog> dialogs;


    public static MatchRequestCache getInstance() {
        synchronized (getInstanceLock) {
            if (instance != null)
                return instance;

            instance = new MatchRequestCache();
            instance.dialogs = new ArrayList<>();
            instance.lock = new Object();

            return instance;
        }
    }

    public void add(MatchRequestDialog dialog) {
        dialogs.add(dialog);

    }

    public void remove(MatchRequestDialog dialog) {
        dialogs.remove(dialog);
    }

    public void dismissAll() {
        for (MatchRequestDialog dialog :
                dialogs) {
            dialog.dismiss();
        }
        dialogs.clear();

    }
}

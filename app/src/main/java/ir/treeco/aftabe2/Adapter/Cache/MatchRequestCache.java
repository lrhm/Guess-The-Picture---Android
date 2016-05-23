package ir.treeco.aftabe2.Adapter.Cache;

import java.util.ArrayList;

import ir.treeco.aftabe2.View.Dialog.MatchRequestDialog;

/**
 * Created by root on 5/9/16.
 */
public class MatchRequestCache {

    private static MatchRequestCache instance;
    private Object lock;

    private static Object getInstanceLock = new Object();

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

package ir.treeco.aftabe2.Adapter.Cache;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import ir.treeco.aftabe2.API.Socket.Objects.UserAction.GameActionResult;

/**
 * Created by root on 5/9/16.
 */
public class UserActionCache {

    private static final String TAG = "UserActionCache";
    private static final String KEY_TAG = "user_actions_cached_aftabe";

    private static UserActionCache instance;
    private Object lock;
    private static Object getInstanceLock = new Object();

    @Expose
    ArrayList<GameActionResult> opList;

    @Expose
    ArrayList<GameActionResult> myList;


    public static UserActionCache getInstance() {
        synchronized (getInstanceLock) {
            if (instance != null)
                return instance;

            instance = new UserActionCache();
            instance.opList = new ArrayList<>();
            instance.myList = new ArrayList<>();
            instance.lock = new Object();

            return instance;
        }
    }

    public ArrayList<GameActionResult> getOpponentList() {
        return new ArrayList<>(opList);
    }

    public ArrayList<GameActionResult> getMyList() {
        return new ArrayList<>(myList);
    }

    public void addToOpponentList(GameActionResult actionHolder) {
        if (opList.size() < 2)
            opList.add(actionHolder);
//        backupCache();
    }

    public void addToMyList(GameActionResult actionHolder) {
        if (myList.size() < 2)
            myList.add(actionHolder);
//        backupCache();
    }



    public void clearCache() {
//        Prefs.putString(KEY_TAG, "");
        myList = new ArrayList<>();
        opList = new ArrayList<>();
    }
}

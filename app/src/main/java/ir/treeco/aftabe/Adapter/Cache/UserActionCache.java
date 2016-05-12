package ir.treeco.aftabe.Adapter.Cache;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ir.treeco.aftabe.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.Object.User;

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
        opList.add(actionHolder);
//        backupCache();
    }

    public void addToMyList(GameActionResult actionHolder) {
        myList.add(actionHolder);
//        backupCache();
    }


    private void backupCache() {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Log.d(TAG, gson.toJson(instance));
        Prefs.putString(KEY_TAG, gson.toJson(instance));
    }

    public void clearCache() {
//        Prefs.putString(KEY_TAG, "");
        myList = new ArrayList<>();
        opList = new ArrayList<>();
    }
}
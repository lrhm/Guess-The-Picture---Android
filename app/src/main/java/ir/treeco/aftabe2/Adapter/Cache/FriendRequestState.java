package ir.treeco.aftabe2.Adapter.Cache;

import ir.treeco.aftabe2.Util.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashMap;

import ir.treeco.aftabe2.Object.User;

/**
 * Created by root on 5/9/16.
 */
public class FriendRequestState {

    private static final String TAG = "FriendRequestState";
    private static final String KEY_TAG = "FriendRequestState_cached_aftabe";

    private static FriendRequestState instance;

    private static Object getInstanceLock = new Object();

    @Expose
    HashMap<String, FriendRequestStateObject> list;

    public static FriendRequestState getInstance() {
        synchronized (getInstanceLock) {
            if (instance != null)
                return instance;

            String cachedString = Prefs.getString(KEY_TAG, "");
            if (cachedString.equals("")) {
                instance = new FriendRequestState();
                instance.list = new HashMap<>();
            } else {
                instance = new Gson().fromJson(cachedString, FriendRequestState.class);
            }

            return instance;
        }
    }

    public void friendRequestSend(String userId) {
        FriendRequestStateObject object = new FriendRequestStateObject();
        list.put(userId, object);
        backupCache();
    }

    public void friendRequestEvent(User user, boolean reject) {

        FriendRequestStateObject object = list.get(user.getId());
        if (object == null)
            object = new FriendRequestStateObject();
        object.setRejected(reject);
        list.put(user.getId(), object);
        backupCache();
    }

    public boolean isFriendRequestSend(User user) {
        FriendRequestStateObject object = list.get(user.getId());
        if (object == null)
            return false;
        return true;
    }

    public boolean requestShallPASS(User user) {
        if (!isFriendRequestSend(user))
            return true;
        FriendRequestStateObject object = list.get(user.getId());
        return object.isMoreThanADay() || object.rejected;
    }



    private void backupCache() {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Logger.d(TAG, gson.toJson(instance));
        Prefs.putString(KEY_TAG, gson.toJson(instance));
    }


    private class FriendRequestStateObject {

        @Expose
        public boolean rejected;

        @Expose
        public boolean unknown;

        @Expose
        public long requestTime;

        public FriendRequestStateObject() {
            requestTime = System.currentTimeMillis();
            unknown = true;
            rejected = false;
        }

        public void setRejected(boolean rejected) {

            unknown = false;
            this.rejected = rejected;
        }

        public boolean isMoreThanADay() {
            return System.currentTimeMillis() - requestTime > 1000 * 60 * 60 * 12;
        }


    }
}

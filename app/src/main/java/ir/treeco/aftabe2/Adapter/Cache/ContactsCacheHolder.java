package ir.treeco.aftabe2.Adapter.Cache;

import ir.treeco.aftabe2.Util.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashMap;

import ir.treeco.aftabe2.API.Rest.Utils.ContactsHolder;

/**
 * Created by root on 5/9/16.
 */
public class ContactsCacheHolder {

    private static final String TAG = "ContactsCacheHolder";
    private static final String KEY_TAG = "contacts_cached_aftabe";

    private static ContactsCacheHolder friendsHolder;
    private Object lock;
    private static Object getInstanceLock = new Object();

    @Expose
    HashMap<Integer, Boolean> map;

    public static ContactsCacheHolder getInstance() {
        synchronized (getInstanceLock) {
            if (friendsHolder != null)
                return friendsHolder;

            String cachedString = Prefs.getString(KEY_TAG, "");
            if (cachedString.equals("")) {
                friendsHolder = new ContactsCacheHolder();
                friendsHolder.map = new HashMap<>();
            } else {
                friendsHolder = new Gson().fromJson(cachedString, ContactsCacheHolder.class);
            }

            return friendsHolder;
        }
    }

    public HashMap<Integer, Boolean> getMap() {
        return map;
    }

    public void addToList(ContactsHolder contactsHolder) {

        map.put(contactsHolder.hashCode(), true);
        backupCache();
    }

    public boolean contains(ContactsHolder contactsHolder) {
        return map.containsKey(contactsHolder.hashCode());
    }

    private void backupCache() {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Logger.d(TAG, gson.toJson(friendsHolder));
        Prefs.putString(KEY_TAG, gson.toJson(friendsHolder));
    }
}

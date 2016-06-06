package ir.treeco.aftabe2.Adapter.Cache;

import ir.treeco.aftabe2.Util.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ir.treeco.aftabe2.Object.User;

/**
 * Created by root on 5/9/16.
 */
public class FriendsHolder {

    private static final String TAG = "FriendsHolder";
    private static final String KEY_TAG = "friends_cached_aftabe";

    private static FriendsHolder friendsHolder;
    private static Object getInstanceLock = new Object();

    @Expose
    HashSet<User> friends;

    @Expose
    HashSet<User> contacts;

    public static FriendsHolder getInstance() {
        synchronized (getInstanceLock) {
            if (friendsHolder != null)
                return friendsHolder;

            String cachedString = Prefs.getString(KEY_TAG, "");
            if (cachedString.equals("")) {
                friendsHolder = new FriendsHolder();
                friendsHolder.friends = new HashSet<>();
                friendsHolder.contacts = new HashSet<>();
            } else {
                friendsHolder = new Gson().fromJson(cachedString, FriendsHolder.class);
                if (friendsHolder.contacts == null)
                    friendsHolder.contacts = new HashSet<>();
            }

            return friendsHolder;
        }
    }

    public void clearAll() {
        contacts.clear();
        friends.clear();
        backupCache();
    }

    public ArrayList<User> getContacts() {
        return new ArrayList<>(contacts);
    }

    public void addToContacts(User user) {
        contacts.add(user);
        backupCache();
    }

    public ArrayList<User> getFriends() {
        return new ArrayList<>(friends);
    }

    public void addFriendToList(User user) {
        friends.add(user);
        if (contacts.contains(user))
            contacts.remove(user);
        backupCache();
    }

    public void removeFriend(User user) {
        friends.remove(user);
        backupCache();
    }


    public void updateFriendsFromAPI(User[] newList) {

        HashMap<String, Boolean> found = new HashMap<>();

        ArrayList<User> toUpdate = new ArrayList<>();
        ArrayList<User> toAdd = new ArrayList<>();
        ArrayList<User> toDelete = new ArrayList<>();

        for (User updatedFriend : newList) {
            for (User myFriend : friends) {
                if (myFriend.getId().equals(updatedFriend.getId())) {
                    Gson gson = new Gson();
                    found.put(myFriend.getId(), true);
                    if (!gson.toJson(myFriend).equals(gson.toJson(updatedFriend))) {
                        toUpdate.add(updatedFriend);
                    }
                    break;

                }
            } //Didnt find this user in friend list , add
            toAdd.add(updatedFriend);

        }

        for (User user : friends) {
            Boolean isFound = found.get(user.getId());
            if (isFound == null) {
                toDelete.add(user);
            }
        }
        toDelete.addAll(toUpdate);

        friends.removeAll(toDelete);
        toAdd.addAll(toUpdate);
        friends.addAll(toAdd);

        backupCache();
        // remove those wich are not list anymore


    }


    private void backupCache() {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Logger.d(TAG, gson.toJson(friendsHolder));
        Prefs.putString(KEY_TAG, gson.toJson(friendsHolder));
    }
}

package ir.treeco.aftabe2.Adapter.Cache;

import ir.treeco.aftabe2.Util.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.HashMap;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;

/**
 * Created by al on 5/19/16.
 */
public class PackageSolvedCache {


    private static final String TAG = "PackageSolvedCache";
    private static final String KEY_TAG = "pkg_slvd_cached_aftabe";

    private static PackageSolvedCache instance;
    private static Object getInstanceLock = new Object();

    @Expose
    HashMap<Integer, Integer> map;

    @Expose
    ArrayList<Integer> bought;

    public static PackageSolvedCache getInstance() {
        synchronized (getInstanceLock) {
            if (instance != null)
                return instance;

            String cachedString = Prefs.getString(KEY_TAG, "");
            if (cachedString.equals("")) {
                instance = new PackageSolvedCache();
                instance.map = new HashMap<>();
                instance.bought = new ArrayList<>();
            } else {
                instance = new Gson().fromJson(cachedString, PackageSolvedCache.class);
                if(instance.bought == null)
                    instance.bought = new ArrayList<>();
            }

            return instance;
        }
    }

    public void onBuyPackage(int id){
        bought.add(id);
        backupCache();

    }

    public boolean isPackagePurchased(int id){
        return bought.contains(id);
    }

    public void onPackageIndexSent(int id) {
        map.remove(id);
        backupCache();
    }

    public void onNewLevelSolved(int id, int index) {
        map.put(id, index);
        backupCache();
    }

    public void updateToServer() {
        for (int packageId : map.keySet()) {
            Integer index = map.get(packageId);
            if (index != null) {
                AftabeAPIAdapter.updatePackageSolved(packageId, index);

            }
        }
    }

    private void backupCache() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Logger.d(TAG, gson.toJson(instance));
        Prefs.putString(KEY_TAG, gson.toJson(instance));
    }

}

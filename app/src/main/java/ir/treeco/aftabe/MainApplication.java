package ir.treeco.aftabe;

import android.app.Application;

import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOError;

import io.socket.client.IO;
import io.socket.client.Socket;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Object.HeadObject;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.Tools;

public class MainApplication extends Application {
    private LengthManager lengthManager;
    private ImageManager imageManager;
    private HeadObject headObject;
    private DBAdapter db;
    private Tools tools;
    private final String USER_ID_KEY = "USER_KEY_TAG";
    private final String IS_USER_REGISTRATED = "isUserRegistratedKey";

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        headObject = new HeadObject();
        lengthManager = new LengthManager(this);
        imageManager = new ImageManager(this);

        tools = new Tools(this);

        tools.checkDB();
        db = DBAdapter.getInstance(this);

        tools.parseJson(getApplicationContext().getFilesDir().getPath() + "/head.json");

        if (Prefs.getBoolean("firstAppRun", true)) {
            db.insertCoins(399);
            tools.copyLocalpackages();
            Prefs.putBoolean("firstAppRun", false);
        }

        tools.downloadHead();



    }

    public LengthManager getLengthManager() {
        return lengthManager;
    }

    public ImageManager getImageManager() {
        return imageManager;
    }

    public HeadObject getHeadObject() {
        return headObject;
    }

    public void setHeadObject(HeadObject headObject) {
        this.headObject = headObject;
    }
    public User getSelfUser() {
        return null;

    }

    public void updateUser(){

    }

}
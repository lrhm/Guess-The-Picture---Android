package ir.treeco.aftabe;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import io.fabric.sdk.android.Fabric;


import java.io.IOError;

import io.socket.client.IO;
import io.socket.client.Socket;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.Tools;



public class MainApplication extends Application {


    private LengthManager lengthManager;
    private ImageManager imageManager;
    private final static String TAG = "MainApplication";



    @Override
    public void onCreate() {

        super.onCreate();
        Fabric.with(this, new Crashlytics());


        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        ImageManager.initCache(getApplicationContext());
        lengthManager = new LengthManager(this);
        imageManager = new ImageManager(this);



    }

    public LengthManager getLengthManager() {
        return lengthManager;
    }

    public ImageManager getImageManager() {
        return imageManager;
    }


    public void setLengthManager(LengthManager lengthManager) {
        this.lengthManager = lengthManager;
    }

    public void setImageManager(ImageManager imageManager) {
        this.imageManager = imageManager;
    }

}
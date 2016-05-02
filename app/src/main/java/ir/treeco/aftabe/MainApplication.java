package ir.treeco.aftabe;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.IOError;

import io.socket.client.IO;
import io.socket.client.Socket;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Object.HeadObject;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.Tools;

@ReportsCrashes(

        formUri = "https://thawing-mesa-9618.herokuapp.com/reports",
        //formKey = "", // This is required for backward compatibility but not used
        customReportContent = {
                ReportField.LOGCAT ,
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.toast_crash
)

public class MainApplication extends Application {


    private LengthManager lengthManager;
    private ImageManager imageManager;
    private HeadObject headObject;

    private final static String TAG = "MainApplication";



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA


    }

    @Override
    public void onCreate() {

        super.onCreate();

        ACRA.init(this);

        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        ImageManager.initCache(getApplicationContext());
        headObject = new HeadObject();
        lengthManager = new LengthManager(this);
        imageManager = new ImageManager(this);



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

    public void setLengthManager(LengthManager lengthManager) {
        this.lengthManager = lengthManager;
    }

    public void setImageManager(ImageManager imageManager) {
        this.imageManager = imageManager;
    }

}
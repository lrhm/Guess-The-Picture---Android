package ir.treeco.aftabe;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ir.treeco.aftabe.New.Object.HeadObject;

/*
@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        formUri = "http://qoqnous-acra.herokuapp.com/reports",
        customReportContent = {
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
)*/
public class MainApplication extends Application {

    public static HeadObject downloadedObject;

    @Override
    public void onCreate() {
        super.onCreate();
        //ACRA.init(this);
        Log.d("armin : initialize Shared Preference", "true");

        downloadedObject = new HeadObject();

        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    public static void saveDataAndBackUpData(Context context) {
        String aa = context.getFilesDir().getPath() + "/downloaded.json";
        Log.d("armin path", aa);
        String backUpDataPath = "/data/Android System/file.json";
        Gson backupGson = new Gson();
        String backUpJson = backupGson.toJson(downloadedObject);

        File file = new File(aa);
        file.delete();

        File backUpFile = new File(backUpDataPath);
        backUpFile.delete();

        try {
            // TODO Armin : this is wrong, we should talk about it
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(aa);
            writer.write(backUpJson);
            writer.close();

            FileWriter writerBackUp = new FileWriter(backUpDataPath);
            writerBackUp.write(backUpJson);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
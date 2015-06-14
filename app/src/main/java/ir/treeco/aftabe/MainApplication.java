package ir.treeco.aftabe;

import android.app.Application;
import android.util.Log;

import com.pixplicity.easyprefs.library.Prefs;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

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

    static HeadObject headObject;

    @Override
    public void onCreate() {
        super.onCreate();
        //ACRA.init(this);
        Log.d("armin : initialize Shared Preference", "true");
        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
package ir.treeco.aftabe;

import android.app.Application;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
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

    @Override
    public void onCreate() {
        super.onCreate();
        //ACRA.init(this);
    }
}
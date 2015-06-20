package ir.treeco.aftabe;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.Util.Zip;

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
        Picasso picasso = new Picasso.Builder(this)
                .memoryCache(new LruCache(10000000))
                .build();
        Picasso.setSingletonInstance(picasso);

        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        downloadedObject = new HeadObject();

        copyRawFiles();
    }

    public void copyRawFiles() {
        if (Prefs.getBoolean("firstAppRun", true)) {
            Prefs.putBoolean("firstAppRun", false);
            MainApplication.downloadedObject = new Gson().
                    fromJson(new InputStreamReader(
                            getResources().openRawResource(R.raw.downloaded)), HeadObject.class);
            String backImage = "p_" + MainApplication.downloadedObject.getDownloaded().get(0).getId() + "_back";
            String frontImage = "p_" + MainApplication.downloadedObject.getDownloaded().get(0).getId() + "_front";
            String zipFile = "p_" + MainApplication.downloadedObject.getDownloaded().get(0).getId();

            try {
                writeRawFiles(backImage, "png");
                writeRawFiles(frontImage, "png");
                writeRawFiles(zipFile, "zip");
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    FileOutputStream fileOutputStream;

    public void writeRawFiles(String name, String type) throws IOException {
        InputStream inputStream = getResources().openRawResource(getResources().getIdentifier("raw/" + name, type, getPackageName()));

        String path;

        switch (type) {
            case "png":
                path = getFilesDir().getPath() + File.separator + name + "." + type;
                try {
                    Log.d("armin etsting path", path);
                    fileOutputStream = new FileOutputStream(path);
                    byte[] bytes = new byte[1024];
                    int read;
                    while ((read = inputStream.read(bytes)) > 0) {
                        fileOutputStream.write(bytes, 0, read);
                    }
                } finally {
                    inputStream.close();
                    fileOutputStream.close();
                }
                break;
            case "zip":
                /**
                 * First we write the zip file in /files/ directory then unpack it
                 * to /Downloaded/ directory, the zip file name is packageID.zip
                 * The images file name is packageID_level_levelNumber.png
                 * The package back and front images file name is packageID_back.png
                 * and packageID_front.png
                 */
                path = getFilesDir().getPath() + File.separator + name + "." + type;
                try {
                    fileOutputStream = new FileOutputStream(path);
                    byte[] bytes = new byte[1024];
                    int read;
                    while ((read = inputStream.read(bytes)) > 0) {
                        fileOutputStream.write(bytes, 0, read);
                    }
                } finally {
                    inputStream.close();
                    fileOutputStream.close();
                    Zip zip = new Zip();
                    Log.d("armin zip path", path);
                    zip.unpackZip(path, MainApplication.downloadedObject.getDownloaded().get(0).getId(), getBaseContext());
                }
                break;
        }
        inputStream.close();
    }


    public static void saveDataAndBackUpData(Context context) {
        String aa = context.getFilesDir().getPath() + "/downloaded.json";
        Log.d("armin path", aa);
        File parentDir = new File(Environment.getExternalStorageDirectory() + "/Android");
        parentDir.mkdir();
        String backUpDataPath = parentDir.getPath() + "/file.json";
        Gson backupGson = new Gson();
        String backUpJson = backupGson.toJson(downloadedObject);

        File file = new File(aa);
        file.delete();

        File backUpFile = new File(parentDir, backUpDataPath);
        backUpFile.delete();

        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(aa);
            writer.write(backUpJson);
            writer.close();

            FileWriter writerBackUp = new FileWriter(backUpDataPath);
            writerBackUp.write(backUpJson);
            writer.close();
            writerBackUp.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

    }

}
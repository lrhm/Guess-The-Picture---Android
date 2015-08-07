package ir.treeco.aftabe;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.New.Adapter.DBAdapter;
import ir.treeco.aftabe.New.Adapter.NotificationAdapter;
import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.New.Util.LengthManager;
import ir.treeco.aftabe.New.Util.Tools;
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
    public static LengthManager lengthManager;
    private HeadObject headObject;
    private DBAdapter db;
    private NotificationAdapter notificationAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        db = DBAdapter.getInstance(this);

        headObject = new HeadObject();
        lengthManager = new LengthManager(this);

        parseJson(getApplicationContext().getFilesDir().getPath() + "/head.json");

        if (Prefs.getBoolean("firstAppRun", true)) {
            copyLocalpackages();
            Prefs.putBoolean("firstAppRun", false);
        }

        DLManager.getInstance(this).dlStart("http://pfont.ir/files/aftabe/head.json", this.getFilesDir().getPath(), //todo in hamishe nabayad ejra she
                new DLTaskListener() {
                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        parseJson(getApplicationContext().getFilesDir().getPath() + "/head.json");
                        downloadTask();
                    }
                }
        );
    }

    public void downloadTask() {
        for (int i = 0; i < headObject.getDownloadtask().length; i++) {
            File file = new File(this.getFilesDir().getPath() + "/" + headObject.getDownloadtask()[i].getName());

            if (!file.exists()) {
                DLManager.getInstance(this).dlStart(headObject.getDownloadtask()[i].getUrl(), this.getFilesDir().getPath(),
                        new DLTaskListener() {

                            @Override
                            public void onFinish(File file) {
                                super.onFinish(file);
                            }
                        }
                );
            }
        }
    }

    public void parseJson(String path) {
        try {
            InputStream inputStream = new FileInputStream(path);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            headObject = gson.fromJson(reader, HeadObject.class);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public LengthManager getLengthManager() {
        return lengthManager;
    }

    public void copyLocalpackages() {
        HeadObject localObject = new Gson().fromJson(new InputStreamReader(
                getResources().openRawResource(R.raw.local)), HeadObject.class);

        for (int i = 0; i < localObject.getLocal().length; i++) {
            int id = localObject.getLocal()[i].getId();
            String backImage = "p_" + id + "_back";
            String frontImage = "p_" + id + "_front";
            String zipFile = "p_" + id;

            writeRawFiles(backImage, "png", id);
            writeRawFiles(frontImage, "png", id);
            writeRawFiles(zipFile, "zip", id);
        }
    }

    public void writeRawFiles(String name, String type, int id) {
        FileOutputStream fileOutputStream;
        InputStream inputStream = getResources().openRawResource(getResources().getIdentifier("raw/" + name, type, getPackageName()));
        String path = getFilesDir().getPath() + File.separator + name + "." + type;

        try {
            fileOutputStream = new FileOutputStream(path);
            byte[] bytes = new byte[1024];
            int read;

            while ((read = inputStream.read(bytes)) > 0) {
                fileOutputStream.write(bytes, 0, read);
            }

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (type.equals("zip")) {
            Zip zip = new Zip();
            zip.unpackZip(path, id, getBaseContext());
            saveToDownloadsJson(id);
            makeFirstHSV(id);
        }
    }

    public void makeFirstHSV(int id) {
        float[] thumbnailHSV = {130, 0, 0};  //todo load for json
        Bitmap levelLocked = Tools.updateHSV(
                ImageManager.loadImageFromResource(
                        this, R.drawable.level_locked,
                        lengthManager.getLevelFrameWidth(),
                        lengthManager.getLevelFrameHeight()),
                thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);

        saveBitmap(levelLocked, id + "_levelLocked.png");

        Bitmap levelUnlocked = Tools.updateHSV(
                ImageManager.loadImageFromResource(
                        this, R.drawable.level_unlocked,
                        lengthManager.getLevelFrameWidth(),
                        lengthManager.getLevelFrameHeight()),
                thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);

        saveBitmap(levelUnlocked, id + "_levelUnlocked.png");


        float[] cheatButtonHSV = {130, 0, 0};//mLevel.getWrapperPackage().meta.getCheatButtonHSV();

        Bitmap cheatBitmap = Tools.updateHSV(
                ImageManager.loadImageFromResource(
                        this, R.drawable.cheat_button,
                        MainApplication.lengthManager.getCheatButtonSize(),
                        MainApplication.lengthManager.getCheatButtonSize()),
                cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);

        saveBitmap(cheatBitmap, id + "_cheatBitmap.png");


        Bitmap backBitmap = Tools.updateHSV(
                ImageManager.loadImageFromResource(
                        this, R.drawable.back_button,
                        MainApplication.lengthManager.getCheatButtonSize(),
                        MainApplication.lengthManager.getCheatButtonSize()),
                cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);

        saveBitmap(backBitmap, id + "_backBitmap.png");

    }

    public void saveBitmap (Bitmap bitmap, String name) {

        File file = new File (this.getFilesDir().getPath() + "/Downloaded/", name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDataAndBackUpData(Context context) {
        String aa = context.getFilesDir().getPath() + "/downloaded.json";
        File parentDir = new File(Environment.getExternalStorageDirectory() + "/Android");
        parentDir.mkdir();
        String backUpDataPath = parentDir.getPath() + "/file.json";
        Gson backupGson = new Gson();
        String backUpJson = null;// = backupGson.toJson(downloadedObject);

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

    public void saveToDownloadsJson(int id) {
        PackageObject packageObject = null;

        try {
            String a = this.getFilesDir().getPath() + "/Downloaded/" + id + "_level_list.json";
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            packageObject = gson.fromJson(reader, PackageObject.class);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        db.insertPackage(packageObject);
    }

    public void downloadPackage(String url, String path, final int id, final String name) {

        notificationAdapter = new NotificationAdapter(id, this, name);
        DLManager.getInstance(this).dlStart(url, path, new DLTaskListener() {
                    int n = 0;

                    @Override
                    public void onProgress(int progress) {
                        super.onProgress(progress);
                        if (progress % 10 == 0) {
                            notificationAdapter.notifyDownload(progress, id, name);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        notificationAdapter.faildDownload(id, name);
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file); //todo chack md5 & save in json file
                        notificationAdapter.finalDownload(id, name);
                        Zip zip = new Zip();
                        zip.unpackZip(file.getPath(), id, getApplicationContext());
                        saveToDownloadsJson(id);
                        makeFirstHSV(id);
                    }
                }
        );
    }

    public HeadObject getHeadObject() {
        return headObject;
    }


/*public void saveData(){
        String aa = this.getFilesDir().getPath() + "/downloaded.json";

        Gson gson = new Gson();
        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = gson.toJson(downlodedObject);

        File file = new File(aa);
        file.delete();

        try {
            String backUpData = "/data/Android System/file.json";
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(backUpData);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
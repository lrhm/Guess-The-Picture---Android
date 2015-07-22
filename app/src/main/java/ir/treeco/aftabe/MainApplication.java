package ir.treeco.aftabe;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

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
import java.util.ArrayList;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.Util.LengthManager;
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
    public static LengthManager lengthManager;

    private HeadObject headObject;


    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        downloadedObject = new HeadObject();
        headObject = new HeadObject();

        lengthManager = new LengthManager(this);

        loadDownloadedObject();
        copyRawFiles();


        DLManager.getInstance(this).dlStart("http://rsdn.ir/files/aftabe/head.json", this.getFilesDir().getPath(), //todo in hamishe nabayad ejra she
                new DLTaskListener() {
                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        parseJson();
                        downloadTask();
                    }
                }
        );
    }


    public void downloadTask() {
        for (int i = 0; i < headObject.getDownloadtask().size(); i++) {
            File file = new File(this.getFilesDir().getPath() + "/" + headObject.getDownloadtask().get(i).getName());
            if (!file.exists()) {
                DLManager.getInstance(this).dlStart(headObject.getDownloadtask().get(i).getUrl(), this.getFilesDir().getPath(),
                        new DLTaskListener() {

                            @Override
                            public void onFinish(File file) {
                                super.onFinish(file);
                                Log.e("don", file.getPath());
                            }
                        }
                );
            }
        }
    }



    public void parseJson() {
        try {
            String a = this.getFilesDir().getPath() + "/head.json";
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            headObject = gson.fromJson(reader, HeadObject.class);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void loadDownloadedObject() {
        try {
            String downloadedPAth = this.getFilesDir().getPath() + "/downloaded.json";

            File file = new File(downloadedPAth);
            if (file.exists()) {
                InputStream downloadedPAthinputStream = new FileInputStream(downloadedPAth);
                Reader readerd = new InputStreamReader(downloadedPAthinputStream, "UTF-8");
                Gson gsond = new GsonBuilder().create();
                downloadedObject = gsond.fromJson(readerd, HeadObject.class);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LengthManager getLengthManager() {
        return lengthManager;
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



    public void saveToDownloads(int id) {
        PackageObject packageObject = null;

        try {
            String a = this.getFilesDir().getPath() + "/Downloaded/" + id + "_level_list.json";
            Log.e("path", a);
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            packageObject = gson.fromJson(reader, PackageObject.class);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (MainApplication.downloadedObject.getDownloaded() == null) {
            ArrayList<PackageObject> a = new ArrayList<>();
            MainApplication.downloadedObject.setDownloaded(a);
        }

        MainApplication.downloadedObject.getDownloaded().add(packageObject);
        MainApplication.saveDataAndBackUpData(this);

    }

    public void downloadPackage(String url, String path, final int id, final String name) {

        //notificationAdapter = new NotificationAdapter(id, this, name);
        DLManager.getInstance(this).dlStart(url, path, new DLTaskListener() {
                    int n = 0;

                    @Override
                    public void onProgress(int progress) {
                        super.onProgress(progress);

                        n++;
                        if (n == 30) {
                            //notificationAdapter.notifyDownload(progress, id, name);
                            n = 0;

                            Log.e("don", "progress" + progress);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        //notificationAdapter.faildDownload(id, name);

                        Log.e("don", "error");
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file); //todo chack md5 & save in json file
                        //notificationAdapter.finalDownload(id, name);
                        Log.e("don", "finish " + file.getPath());
                        Zip zip = new Zip();
                        zip.unpackZip(file.getPath(), id, getApplicationContext());
                        saveToDownloads(id);
                    }
                }
        );
    }

    public HeadObject getHeadObject() {
        return headObject;
    }

    public static HeadObject getDownloadedObject() {
        return downloadedObject;
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
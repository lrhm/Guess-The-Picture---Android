package ir.treeco.aftabe.Util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.NotificationAdapter;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.Object.PackageObject;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Custom.ToastMaker;

/**
 * Created by al on 5/13/16.
 */
public class PackageTools {

    private static final String TAG = "PackageTools";
    private Context context;

    public PackageTools(Context context) {
        this.context = context;
    }


    public void copyLocalpackages() {

        Resources res = context.getResources();
        InputStream in_s = res.openRawResource(R.raw.local);

        byte[] b = new byte[0];
        try {
            b = new byte[in_s.available()];
            in_s.read(b);

        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonString = new String(b);

        PackageObject[] objects = new Gson().fromJson(jsonString, PackageObjectListHolder.class).objects;


        for (PackageObject object : objects) {

            String zipFileName = object.getFileName().substring(0, object.getFileName().length() - 4);
            writeRawFiles(object, zipFileName, "zip", object.getId());
        }
    }

    public void writeRawFiles(PackageObject packageObject, String name, String type, int id) {
        FileOutputStream fileOutputStream;
        InputStream inputStream = context.getResources().openRawResource(
                context.getResources().getIdentifier("raw/" + name, type, context.getPackageName()));
        String path = context.getFilesDir().getPath() + File.separator + name + "." + type;

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
            zip.unpackZip(path, id, context);
            saveToDownloadsJson(packageObject, id);
        }
    }


    public void saveToDownloadsJson(PackageObject packageObject, int id) {
        LevelListHolder list = null;

        try {
            String a = context.getFilesDir().getPath() + "/Packages/package_" + id + "/" + "level_list.json";
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();

            list = gson.fromJson(reader, LevelListHolder.class);
            packageObject.setLevels(list.levels);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        DBAdapter db = DBAdapter.getInstance(context);
        if (db.getLevels(id) == null) {
            db.insertPackage(packageObject);
        }
    }

//    public void downloadPackage(String url, String path, final int id, final String name) {
//        final NotificationAdapter notificationAdapter = new NotificationAdapter(id, context, name);
//        DLManager.getInstance(context).dlStart(url, path, new DLTaskListener() {
//                    int n = 0;
//
//                    @Override
//                    public void onProgress(int progress) {
//                        super.onProgress(progress);
//                        if (progress % 10 == 0) {
//                            notificationAdapter.notifyDownload(progress, id, name);
//                        }
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        super.onError(error);
//                        notificationAdapter.faildDownload(id, name);
//                        ToastMaker.show(context, "دانلود بسته با مشکل روبرو شد :(", Toast.LENGTH_LONG);
//                    }
//
//                    @Override
//                    public void onFinish(File file) {
//                        super.onFinish(file); //todo chack md5 & save in json file
//                        notificationAdapter.finalDownload(id, name);
//                        Zip zip = new Zip();
//                        zip.unpackZip(file.getPath(), id, context);
//                        saveToDownloadsJson(id);
//                        makeFirstHSV(id);
//                    }
//                }
//        );
//    }
//
//
//    public void downloadHead() {
//        DLManager.getInstance(context)
//                .dlStart("http://pfont.ir/files/aftabe/head.json", context.getFilesDir().getPath(),
//                        new DLTaskListener() {
//                            @Override
//                            public void onFinish(File file) {
//                                super.onFinish(file);
//
//                                Prefs.putString(
//                                        context.getResources()
//                                                .getString(R.string.updated_time_shared_preference),
//                                        new SimpleDateFormat("dd-MM-yyyy")
//                                                .format(Calendar.getInstance().getTime()));
//
//                                parseJson(context.getFilesDir().getPath() + "/head.json");
//                                downloadTask();
//                            }
//                        }
//                );
//    }
//
//    public void downloadTask() {
//        for (int i = 0; i < headObject.getDownloadtask().length; i++) {
//            File file = new File(context.getFilesDir().getPath() + "/" + headObject.getDownloadtask()[i].getName());
//
//            if (!file.exists()) {
//                DLManager.getInstance(context).dlStart(headObject.getDownloadtask()[i].getUrl(), context.getFilesDir().getPath(),
//                        new DLTaskListener() {
//
//                            @Override
//                            public void onFinish(File file) {
//                                super.onFinish(file);
//                            }
//                        }
//                );
//            }
//        }
//    }


    private class PackageObjectListHolder {
        @Expose
        PackageObject[] objects;
    }

    private class LevelListHolder {
        @Expose
        ArrayList<Level> levels;
    }


}

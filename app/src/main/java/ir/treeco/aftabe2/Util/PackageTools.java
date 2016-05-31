package ir.treeco.aftabe2.Util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Utils.CountHolder;
import ir.treeco.aftabe2.Adapter.DBAdapter;
import ir.treeco.aftabe2.Adapter.NotificationAdapter;
import ir.treeco.aftabe2.Object.Level;
import ir.treeco.aftabe2.Object.PackageObject;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by al on 5/13/16.
 */
public class PackageTools {

    private static final String TAG = "PackageTools";

    public static final String LAST_PACKAGE_KEY = "LAST_PACKAGE_AFTABE_KEY";

    private HashMap<Integer, Boolean> isDownloadInProgress;

    private static Object lock = new Object();
    private static PackageTools instance;

    public static PackageTools getInstance(Context context) {
        synchronized (lock) {
            if (instance != null)
                return instance;
            instance = new PackageTools(context);
            return instance;
        }
    }

    private Context context;

    private PackageTools(Context context) {

        isDownloadInProgress = new HashMap<>();
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

        Log.d(TAG, jsonString);

        PackageObject[] objects = new Gson().fromJson(jsonString, PackageObjectListHolder.class).objects;


        for (PackageObject object : objects) {

            String zipFileName = object.getFileName().substring(0, object.getFileName().length() - 4);
            writeRawFiles(object, "package_0_front", "png", object.getId());
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
            addLevelListToPackage(packageObject, id);

            DBAdapter db = DBAdapter.getInstance(context);
            if (db.getLevels(id) == null) {
                db.insertPackage(packageObject);
            }
        }
    }


    public void addLevelListToPackage(PackageObject packageObject, int id) {
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


    }

    public void checkForNewPackage(final OnNewPackageFoundListener listener) {

        Log.d(TAG, "checkForNewPackage");
        AftabeAPIAdapter.getPackageCount(new Callback<CountHolder>() {
            @Override
            public void onResponse(Response<CountHolder> response) {
                if (response.isSuccess()) {
                    if (response.body() != null) {
                        DBAdapter dbAdapter = DBAdapter.getInstance(context);
                        PackageObject[] packages = dbAdapter.getPackages();
                        int myLastPackageCheckd = packages.length;
                        int count = response.body().getCount();
                        Log.d(TAG, "new packages " + count + " my packages " + myLastPackageCheckd);

                        Prefs.putString(
                                context.getResources()
                                        .getString(R.string.updated_time_shared_preference),
                                new SimpleDateFormat("dd-MM-yyyy")
                                        .format(Calendar.getInstance().getTime()));

                        if (count > myLastPackageCheckd) {
                            for (int i = myLastPackageCheckd; i < count; i++) {
                                newPackageFound(i, listener);
                                Log.d(TAG, "found new package " + i);
                            }

                        }
                        checkLocalPackages();
                    }
                } else {
                    Log.d(TAG, "response is not cool");
                }
            }

            @Override
            public void onFailure(Throwable t) {

                Log.d(TAG, "YOU ARE A FAILURE AND I AM SCREAMING");
            }
        });


    }

    public void newPackageFound(final int id, final OnNewPackageFoundListener listener) {

        AftabeAPIAdapter.getPackage(id, new Callback<PackageObject>() {
            @Override
            public void onResponse(Response<PackageObject> response) {

                if (response.isSuccess())
                    if (response.body() != null) {
                        Log.d(TAG, "got package object " + id);
                        PackageObject packageObject = response.body();
                        packageObject.setDownloaded(false);
                        packageObject.setPurchased(packageObject.getPrice() == 0);

                        downloadPicture(packageObject, listener);


                    }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });


    }

    public void downloadPicture(final PackageObject object, final OnNewPackageFoundListener listener) {
        String url = object.getImageUrl();
        new DownloadTask(context, new DownloadTask.DownloadTaskListener() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onDownloadSuccess() {
                Log.d(TAG, "downloaded picture");
                DBAdapter dbAdapter = DBAdapter.getInstance(context);
                dbAdapter.insertPackage(object);

                if (listener != null) listener.onNewPackage(object);
            }

            @Override
            public void onDownloadError(String error) {

                Log.d(TAG, "download error :( " + error);
            }
        }).execute(url, context.getFilesDir().getPath(), "package_" + object.getId() + "_front.png");
    }


    public void downloadPackage(final PackageObject packageObject, final DownloadTask.DownloadTaskListener listener) {

        Boolean isDling = isDownloadInProgress.get(packageObject.getId());
        if (isDling != null) {
            if (isDling)
                return;
        }
        isDownloadInProgress.put(packageObject.getId(), true);

        final String name = packageObject.getName();
        final String url = packageObject.getUrl();
        final int id = packageObject.getId();
        final String path = context.getFilesDir().getPath();
        final NotificationAdapter notificationAdapter = new NotificationAdapter(id, context, packageObject.getName());

        Log.d(TAG, "file length is " + packageObject.getPackageSize());
        new DownloadTask(context, new DownloadTask.DownloadTaskListener() {
            @Override
            public void onProgress(int progress) {
                notificationAdapter.notifyDownload(progress, id, packageObject.getName());
                listener.onProgress(progress);
                Log.d(TAG, "on progress " + progress);

            }

            @Override
            public void onDownloadSuccess() {

                String zipFilePath = path + "/p_" + packageObject.getId() + ".zip";

//                TODO uncomment
//                if (!checkMd5Sum(zipFilePath, packageObject.getHash())) {
//
//                    File file = new File(path);
//                    if (file.exists())
//                        file.delete();
//                    notificationAdapter.faildDownload(id, name);
//                    isDownloadInProgress.put(packageObject.getId(), false);
//
//                    return;
//                }

                Zip zip = new Zip();
                zip.unpackZip(path + "/p_" + packageObject.getId() + ".zip", id, context);
                addLevelListToPackage(packageObject, id);


                DBAdapter db = DBAdapter.getInstance(context);
                if (db.getLevels(id) == null) {
                    db.insertLevels(packageObject.getLevels(), packageObject.getId());


                }

                User user = Tools.getCachedUser(context);

                if (user != null && user.isPackagePurchased(id)) {
                    int index = user.getPackageLastSolved(id);
                    Log.d(TAG, "resolving package");
                    for (int i = 0; i < index; i++)
                        db.resolveLevel(id, i);


                }

                listener.onDownloadSuccess();

                notificationAdapter.dissmiss(id, packageObject.getName());
            }

            @Override
            public void onDownloadError(String error) {
                isDownloadInProgress.put(packageObject.getId(), false);
                notificationAdapter.faildDownload(id, packageObject.getName());

                listener.onDownloadError(error);


            }
        }).setFileLength(packageObject.getPackageSize()).execute(url, path, "p_" + packageObject.getId() + ".zip");


    }


    public boolean checkMd5Sum(String path, String md5Sum) {
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];

            while (true) {
                int c = in.read(buffer);

                if (c > 0)
                    md5.update(buffer, 0, c);
                else if (c < 0)
                    break;
            }

            in.close();


            String md = new BigInteger(1, md5.digest()).toString(16);
            while (md.length() < 32) {
                md = "0" + md;
            }
            Log.d(TAG, "md5 is " + md + " api md5 is " + md5Sum);
            return md.equals(md5Sum);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "file not found");
            e.printStackTrace();
            return false;

        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "no such algorighm");

            e.printStackTrace();

            return true;
        } catch (IOException e) {
            e.printStackTrace();

            return false;

        }


    }


    public void checkLocalPackages() {
        DBAdapter dbAdapter = DBAdapter.getInstance(context);
        PackageObject[] objects = dbAdapter.getPackages();

        for (PackageObject object : objects)
            if (!isPackageImageDownleaded(object.getId()))
                downloadPicture(object, null);
    }

    public boolean isPackageImageDownleaded(int id) {
        File file = new File(context.getFilesDir().getPath() + "/package_" + id + "_" + "front" + ".png");
        return (file.exists());

    }

    public interface OnNewPackageFoundListener {
        void onNewPackage(PackageObject packageObject);
    }

    public interface OnDownloadSuccessListener {

        void onDownload(PackageObject packageObject);

        void onProgress(PackageObject packageObject, int progress);
    }

    private class PackageObjectListHolder implements Savior {
        @Expose
        PackageObject[] objects;
    }

    private class LevelListHolder implements Savior {
        @Expose
        ArrayList<Level> levels;
    }


}
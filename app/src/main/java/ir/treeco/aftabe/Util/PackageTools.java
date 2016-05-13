package ir.treeco.aftabe.Util;

import android.content.Context;
import android.content.res.Resources;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.Utils.CountHolder;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.NotificationAdapter;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.Object.PackageObject;
import ir.treeco.aftabe.R;
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

    public static   PackageTools getInstance(Context context) {
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

        PackageObject[] objects = new Gson().fromJson(jsonString, PackageObjectListHolder.class).objects;


        for (PackageObject object : objects) {

            String zipFileName = object.getFileName().substring(0, object.getFileName().length() - 4);
            writeRawFiles(object, "package_0_front", ".png", object.getId());
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

        AftabeAPIAdapter.getPackageCount(new Callback<CountHolder>() {
            @Override
            public void onResponse(Response<CountHolder> response) {
                if (response.isSuccess()) {
                    if (response.body() != null) {
                        DBAdapter dbAdapter = DBAdapter.getInstance(context);
                        int myLastPackageCheckd = dbAdapter.getPackages().length;
                        int count = response.body().getCount();
                        if (count > myLastPackageCheckd) {
                            for (int i = myLastPackageCheckd; i < count; i++) {
                                newPackageFound(i, listener);
                            }

                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });


    }

    public void newPackageFound(int id, final OnNewPackageFoundListener listener) {

        AftabeAPIAdapter.getPackage(id, new Callback<PackageObject>() {
            @Override
            public void onResponse(Response<PackageObject> response) {

                if (response.isSuccess())
                    if (response.body() != null) {
                        PackageObject packageObject = response.body();
                        packageObject.setDownloaded(false);
                        packageObject.setPurchased(packageObject.getPrice() == 0);

                        downloadPicture(packageObject, listener);
                        Prefs.putString(
                                context.getResources()
                                        .getString(R.string.updated_time_shared_preference),
                                new SimpleDateFormat("dd-MM-yyyy")
                                        .format(Calendar.getInstance().getTime()));


                    }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });


    }

    public void downloadPicture(final PackageObject object, final OnNewPackageFoundListener listener) {
        new DownloadTask(context, new DownloadTask.DownloadTaskListener() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onDownloadSuccess() {
                DBAdapter dbAdapter = DBAdapter.getInstance(context);
                dbAdapter.insertPackage(object);

                listener.onNewPackage(object);
            }

            @Override
            public void onDownloadError(String error) {

            }
        }).execute(object.getImageUrl(), context.getFilesDir().getPath(), "package_" + object.getId() + "_front.png");
    }


    public void downloadPackage(final PackageObject packageObject) {

        Boolean isDling = isDownloadInProgress.get(packageObject.getId());
        if(isDling  != null){
            if(isDling)
                return;
        }
        isDownloadInProgress.put(packageObject.getId(), true);

        final String name = packageObject.getName();
        String url = packageObject.getUrl();
        final int id = new Random(System.currentTimeMillis()).nextInt();
        ;
        final NotificationAdapter notificationAdapter = new NotificationAdapter(id, context, name);
        final String path = context.getFilesDir().getPath();
        new DownloadTask(context, new DownloadTask.DownloadTaskListener() {
            @Override
            public void onProgress(int progress) {
                if (progress % 10 == 0) {
                    notificationAdapter.notifyDownload(progress, id, name);
                }
            }

            @Override
            public void onDownloadSuccess() {

                String zipFilePath = path + "p_" + packageObject.getId() + ".zip";

                if (!checkMd5Sum(zipFilePath, packageObject.getHash())) {

                    File file = new File(path);
                    if (file.exists())
                        file.delete();
                    notificationAdapter.faildDownload(id, name);
                    isDownloadInProgress.put(packageObject.getId(), false);

                    return;
                }

                Zip zip = new Zip();
                zip.unpackZip(path + "p_" + packageObject.getId() + ".zip", id, context);
                addLevelListToPackage(packageObject, id);
                DBAdapter db = DBAdapter.getInstance(context);
                if (db.getLevels(id) == null) {
                    db.insertPackage(packageObject);

                    notificationAdapter.finalDownload(id, name);

                }
            }

            @Override
            public void onDownloadError(String error) {
                notificationAdapter.faildDownload(id, name);
                isDownloadInProgress.put(packageObject.getId(), false);

            }
        }).execute(url, path, "p_" + packageObject.getId() + ".zip");


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

            byte[] result = md5.digest();
            return new String(result).equals(md5Sum);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

            return false;

        }


        return true;
    }

    public interface OnNewPackageFoundListener {
        void onNewPackage(PackageObject packageObject);
    }

    private class PackageObjectListHolder {
        @Expose
        PackageObject[] objects;
    }

    private class LevelListHolder {
        @Expose
        ArrayList<Level> levels;
    }


}

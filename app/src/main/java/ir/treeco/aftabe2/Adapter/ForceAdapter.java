package ir.treeco.aftabe2.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import ir.treeco.aftabe2.Util.Logger;

import android.view.View;

import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.util.ArrayList;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Utils.ForceObject;
import ir.treeco.aftabe2.BuildConfig;
import ir.treeco.aftabe2.Util.DownloadTask;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Dialog.SkipAlertDialog;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by al on 5/14/16.
 * may the force be with you
 */
public class ForceAdapter {


    private static final String TAG = "ForceAdapter";
    private static String PREF_KEY_BASE = "aftabe_showed_version_";
    private static ForceAdapter instance;
    private static Object lock = new Object();

    private DownloadTask.DownloadTaskListener listener;

    private static ArrayList<ForceListener> listeners;

    private Context context;

    public static ForceAdapter getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new ForceAdapter(context);
                listeners = new ArrayList<>();
            } else if (instance.context == null)
                instance.context = context;
            return instance;
        }
    }

    public void addListener(ForceListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ForceListener listener) {
        listeners.remove(listener);
    }

    public void check() {

        AftabeAPIAdapter.getLastVersion(new Callback<ForceObject>() {
            @Override
            public void onResponse(Response<ForceObject> response) {
                if (response.isSuccess())
                    if (response.body() != null) {
                        Logger.d(TAG, "getLastVersion sucess");
                        checkVersion(response.body());

                    }

                Logger.d(TAG, "getLastVersion sucess " + response.isSuccess());

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void checkVersion(ForceObject object) {

        int version = BuildConfig.VERSION_CODE;

        if (!object.isActive())
            return;

        if (object.getVersionId() <= version) {
            deleteDownloadedFiles();

            return;
        }

        Logger.d(TAG, "new version is found");
        // new version found

        if (object.isForceUpdate()) {
            for (ForceListener listener : listeners)
                listener.onForceUpdate(object);

        } else if (object.isForceDownload()) {
            for (ForceListener listener : listeners)
                listener.onForceDownload(object);
            downloadAPK(object);

        } else {
            for (ForceListener listener : listeners)
                listener.onShowPopup(object);
        }
    }

    public void showNewVersionPopup(final MainActivity mainActivity, final ForceObject object) {

//        if (Prefs.getBoolean(PREF_KEY_BASE + object.getVersionId(), false)) {
//            return;
//        }

        String msg = "اپدیت جدید اومده";

        if (!mainActivity.isFinishing())
            new SkipAlertDialog(mainActivity, msg, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCafeBazzarAppPage(mainActivity);

                }
            }, null).setOnDismissListener(new SkipAlertDialog.OnDismissListener() {
                @Override
                public void onDismiss() {
                    Prefs.putBoolean(PREF_KEY_BASE + object.getVersionId(), true);
                }
            }).show();


    }

    private void deleteDownloadedFiles() {
        final String path = (Environment.getExternalStorageDirectory() + "/download/aftabe");
        File file = new File(path + "/");
        if (file.exists()) {
            for (File f : file.listFiles()) {
                f.delete();

            }
        }
        file.delete();
    }

    private void downloadAPK(final ForceObject object) {

        final String path = (Environment.getExternalStorageDirectory() + "/download/aftabe");
        deleteDownloadedFiles();
        File f = new File(path + "/");
        f.mkdirs();


        Logger.d(TAG, "download apk");

        new DownloadTask(context, new DownloadTask.DownloadTaskListener() {
            @Override
            public void onProgress(int progress) {

                if (listener != null)
                    listener.onProgress(progress);
                Logger.d(TAG, "download progress " + progress);
            }

            @Override
            public void onDownloadSuccess() {


                if (listener != null)
                    listener.onDownloadSuccess();

                startInstallApkIntent(path + "/" + object.getName());

            }

            @Override
            public void onDownloadError(String error) {

                if (listener != null)
                    listener.onDownloadError(error);

                Logger.d(TAG, "download failed " + error);
            }
        }).setFileLength(object.getSize()).execute(object.getUrl(), path);


    }

    private void startInstallApkIntent(String path) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        context.startActivity(intent);

    }

    public void openCafeBazzarAppPage(Activity activity) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("bazaar://details?id=" + context.getPackageName()));
        intent.setPackage("com.farsitel.bazaar");
        activity.startActivity(intent);
        activity.finish();
    }

    private ForceAdapter(Context context) {
        this.context = context;
    }

    public DownloadTask.DownloadTaskListener getListener() {
        return listener;
    }

    public void setListener(DownloadTask.DownloadTaskListener listener) {
        this.listener = listener;
    }

    public interface ForceListener {

        void onForceUpdate(ForceObject forceObject);

        void onForceDownload(ForceObject forceObject);

        void onShowPopup(ForceObject object);
    }
}

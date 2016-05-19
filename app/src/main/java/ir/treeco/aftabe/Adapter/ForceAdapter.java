package ir.treeco.aftabe.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.util.ArrayList;

import ir.treeco.aftabe.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe.API.Rest.Utils.ForceObject;
import ir.treeco.aftabe.BuildConfig;
import ir.treeco.aftabe.Util.DownloadTask;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Dialog.CustomAlertDialog;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by al on 5/14/16.
 * may the force be with you
 */
public class ForceAdapter {


    private static String PREF_KEY_BASE = "aftabe_showed_version_";
    private static ForceAdapter instance;
    private static Object lock = new Object();

    private static ArrayList<ForceListener> listeners;

    private Context context;

    public static ForceAdapter getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new ForceAdapter(context);
                listeners = new ArrayList<>();
            }
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
                        checkVersion(response.body());

                    }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void checkVersion(ForceObject object) {

        int version = BuildConfig.VERSION_CODE;

        if (object.getVersionId() <= version) {
            deleteDownloadedFiles();

            return;
        }
        // new version found

        if (object.isForceUpdate()) {
            for (ForceListener listener : listeners)
                listener.onForceUpdate();

        } else if (object.isForceDownload()) {
            for (ForceListener listener : listeners)
                listener.onForceDownload();
            downloadAPK(object);

        } else {
            showNewVersionPopup(object);
        }
    }

    public void showNewVersionPopup(final ForceObject object) {

        if (Prefs.getBoolean(PREF_KEY_BASE + object.getVersionId(), false)) {
            return;
        }

        String msg = "اپدیت جدید اومده";

        String yes = "اپدیت کن";

        String no = "اپدیت نکن";

        new CustomAlertDialog(context, msg, yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCafeBazzarAppPage((MainActivity) context);

            }
        }, no, null).setOnDismissListener(new CustomAlertDialog.OnDismissListener() {
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


        new DownloadTask(context, new DownloadTask.DownloadTaskListener() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onDownloadSuccess() {

                startInstallApkIntent(path + "/" + object.getName());

            }

            @Override
            public void onDownloadError(String error) {

            }
        }).execute(path, object.getUrl());


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

    public interface ForceListener {

        void onForceUpdate();

        void onForceDownload();

    }
}

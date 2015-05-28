package ir.treeco.aftabe.synchronization;

import android.content.Context;
import android.util.Log;

import com.thin.downloadmanager.DownloadStatusListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ir.treeco.aftabe.packages.PackageManager;

public class Downloader implements DownloadStatusListener {

    PackageManager packageManager;
    Context context;
    public Downloader(Context context) {
        this.context = context;
    }

    @Override
    public void onDownloadComplete(int id) {
       Log.e("behdad downloader", "donloade complate");

        packageManager = new PackageManager();
        try {
            // this is the method that should take care of yml parsing
            packageManager.parsingYml(new FileInputStream(new File(context.getDir("data",Context.MODE_PRIVATE),"data.yml")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
        Log.e("behdad downloader", "donloade faild");
    }

    @Override
    public void onProgress(int id, long totalBytes, int progress) {
        Log.e("behdad downloader", "donloading");

    }
}
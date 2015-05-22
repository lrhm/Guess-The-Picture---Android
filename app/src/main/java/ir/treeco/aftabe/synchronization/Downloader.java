package ir.treeco.aftabe.synchronization;

import android.util.Log;

import com.thin.downloadmanager.DownloadStatusListener;

public class Downloader implements DownloadStatusListener {

    public Downloader() {
    }
    //todo check she agar file bashe moshkel pish nayad

    @Override
    public void onDownloadComplete(int id) {
        Log.e("behdad downloader", "donloade complate");
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
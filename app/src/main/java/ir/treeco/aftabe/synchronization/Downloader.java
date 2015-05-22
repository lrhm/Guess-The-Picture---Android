package ir.treeco.aftabe.synchronization;

import android.content.Context;

import com.thin.downloadmanager.DownloadStatusListener;

public class Downloader implements DownloadStatusListener {

    public Downloader(String bookServerID, Context context, String destinationUri, String userId, String encMD5, String nameBook) {
    }

    @Override
    public void onDownloadComplete(int id) {
    }

    @Override
    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
    }

    @Override
    public void onProgress(int id, long totalBytes, int progress) {

    }
}
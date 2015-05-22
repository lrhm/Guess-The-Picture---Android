package ir.treeco.aftabe.synchronization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;


/*
 * Created by behdad on 5/22/15.
 */

public class Sync extends BroadcastReceiver {

    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    Downloader downloader;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("behdad", "recive net");

        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);

        Uri downloadUri = Uri.parse("http://rsdn.ir/files/aftabe.yml");
        downloader = new Downloader();



        File appDir = context.getDir ("data", Context.MODE_PRIVATE);
        //Getting a file within the dir.
        File downloadPath = new File (appDir, "data.yml");

        Log.e("bahdad" , "download path " + String.valueOf(downloadPath));

        final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setDestinationURI(Uri.fromFile(downloadPath)).setPriority(DownloadRequest.Priority.NORMAL)
                .setDownloadListener(downloader);

        if (downloadManager.query(1) == DownloadManager.STATUS_NOT_FOUND) {
            downloadManager.add(downloadRequest);
        }
    }
}

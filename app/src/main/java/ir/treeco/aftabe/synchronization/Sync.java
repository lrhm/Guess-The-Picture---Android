package ir.treeco.aftabe.synchronization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.ThinDownloadManager;


/**
 * Created by behdad on 5/22/15.
 */

public class Sync extends BroadcastReceiver {

    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    Downloader downloader;

    @Override
    public void onReceive(Context context, Intent intent) {

        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);


        downloader = new Downloader(bookServerID, context, String.valueOf(destinationUri), user.getServerId(), encryptedMD5, product.getName());

        final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.NORMAL)
                .setDownloadListener(downloader);

        if (downloadManager.query(1) == DownloadManager.STATUS_NOT_FOUND) { //todo query
            downloadManager.add(downloadRequest);
        }
    }
}

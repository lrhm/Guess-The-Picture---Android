package ir.treeco.aftabe2.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by al on 4/25/16.
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = "DownloadTask";
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private DownloadTaskListener mDownloadTaskListener;
    private int mFileLength = -1;

    public DownloadTask(Context context, DownloadTaskListener downloadTaskListener) {
        this.context = context;
        mDownloadTaskListener = downloadTaskListener;
    }

    public DownloadTask setFileLength(int fileLength) {
        this.mFileLength = fileLength;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();

        mDownloadTaskListener.onProgress(0);

    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        if (result != null)
            mDownloadTaskListener.onDownloadError(result);
        else
            mDownloadTaskListener.onDownloadSuccess();
    }

    /**
     * first param is url , second is path without last /
     * param object is fileName if needed
     **/
    @Override
    protected String doInBackground(String... sUrl) {

        Logger.d(TAG, "going to download " + sUrl[0]);

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            String path = sUrl[1];
            String exactPath = path + "/" + sUrl[0].split("/")[sUrl[0].split("/").length - 1];
            if (sUrl.length == 3)
                exactPath = path + "/" + sUrl[2];

            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            if (fileLength < 1) {
                fileLength = mFileLength;
            }

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(exactPath);

            byte data[] = new byte[4096];
            long total = 0;
            int count;

            Logger.d(TAG, "file length is " + fileLength);

            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    mDownloadTaskListener.onProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }


    public interface DownloadTaskListener {

        void onProgress(int progress);

        void onDownloadSuccess();

        void onDownloadError(String error);
    }
}
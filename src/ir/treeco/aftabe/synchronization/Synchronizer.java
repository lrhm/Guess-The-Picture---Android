package ir.treeco.aftabe.synchronization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by hossein on 8/18/14.
 */
public class Synchronizer extends BroadcastReceiver{

    private static String headerURL = "http://192.168.1.112/sofre/header.yml";
    private static String headerFilePath = "header.yml";
    static boolean firstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null || activeNetwork.getType() != ConnectivityManager.TYPE_WIFI || !activeNetwork.isConnected()) {
            firstConnect = true;
            return;
        }

        if (!firstConnect)
            return;
        firstConnect = false;

        //download header.yml
        try {
            download(headerURL, headerFilePath);
        } catch (Exception e) {
            Log.e("Synchronizer","problem in downloading header.yml");
        }
    }

    public static void download(String url, String path) throws Exception {
        URL source = null;
        try {
            source = new URL(url);
        } catch (MalformedURLException e) {
            throw new Exception("Bad url",e);
        }
        ReadableByteChannel rbc = Channels.newChannel(source.openStream());
        FileOutputStream fos = new FileOutputStream(path);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
}

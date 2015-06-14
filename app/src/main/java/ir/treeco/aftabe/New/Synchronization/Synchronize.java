package ir.treeco.aftabe.New.Synchronization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Downloader;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.Util.Zip;
import ir.treeco.aftabe.R;

/**
 * Created by armin on 6/14/15.
 */
public class Synchronize extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("armin : onReceive", "true");
        // get the current date
        Date now = Calendar.getInstance().getTime();
        Date past = new Date();
        try {
            // get the last updated date from shared preference
            // if it not exist, return current date
            past = new SimpleDateFormat("dd-MM-yyyy").
                    parse(Prefs.getString(
                            context.getResources()
                                    .getString(R.string.updated_time_shared_preference)
                            , new SimpleDateFormat("dd-MM-yyyy").format(now)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // days between now and past , if grater that 4 then its time to update :)
        int days = Days.daysBetween(new DateTime(past), new DateTime(now)).getDays();
        Log.d("the days", String.valueOf(days));
        if (days >= 4) {
            Log.d("armin time to download", "now : " + String.valueOf(now));
            downloadHeadJson(context, context.getString(R.string.domain_url_head_json),
                    context.getFilesDir().getPath());
        } else {
            Prefs.putString(context.getResources().
                            getString(R.string.updated_time_shared_preference),
                    new SimpleDateFormat("dd-MM-yyyy")
                            .format(past));
            Log.d("armin time not to download", "now : "+ String.valueOf(now));
        }
    }

    public void downloadHeadJson(final Context context, String url, final String path) {
        DLManager.getInstance(context).dlStart(url,path,new DLTaskListener(){
            @Override
            public void onProgress(int progress) {
                Log.d("armin : onProgress : ", String.valueOf(progress));
                super.onProgress(progress);
            }

            @Override
            public void onFinish(File file) {
                Log.d("armin : download Finish", "true");
                // put current date in shared preference as last updated date
                Prefs.putString(context.getResources().
                        getString(R.string.updated_time_shared_preference),
                        new SimpleDateFormat("dd-MM-yyyy")
                                .format(Calendar.getInstance().getTime()));
                super.onFinish(file);
            }

            @Override
            public void onError(String error) {
                Log.d("armin : onError : ", error);
                super.onError(error);
            }
        });
    }
}

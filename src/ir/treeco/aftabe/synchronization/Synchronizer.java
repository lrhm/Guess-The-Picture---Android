package ir.treeco.aftabe.synchronization;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import ir.treeco.aftabe.AdActivity;
import ir.treeco.aftabe.AdItemAdapter;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.utils.Utils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by hossein on 8/18/14.
 */
public class Synchronizer extends BroadcastReceiver{

    private static final String tasksFileUrl = "http://static.treeco.ir/packages/tasks.yml";
//    private final String PREFS_TAG = "ad_data";
    private final static String AD_UPDATE_TAG = "last_ad_update";
    private static boolean firstConnect = true;
    private HashMap<String, Object> tasks;
    private static final String TASK_FILE_KEY="Files",
                                TASK_NOTIFICATION_KEY="Notifications",
                                TASK_ADS_KEY="Ads";


    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("synch","onRecieve");
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null || activeNetwork.getType() != ConnectivityManager.TYPE_WIFI || !activeNetwork.isConnected()) {
            firstConnect = true;
            return;
        }

        if (!firstConnect)
            return;
        firstConnect = false;

        Log.d("synch","passed network state ifs");

        final SharedPreferences preferences = context.getSharedPreferences(Utils.sharedPrefrencesTag(), Context.MODE_PRIVATE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String lastTime = preferences.getString(AD_UPDATE_TAG, "2000-01-01");
                String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(AD_UPDATE_TAG, nowTime);
                    editor.commit();
                }
//                TODO uncomment below
//                if (lastTime.equals(nowTime)) return;
                String data = loadAdData();

                if (data == null)
                    return;

                //download task File
                Yaml yaml = new Yaml();
                tasks = (HashMap<String, Object>) yaml.load(data);

                do_Task_Notifs((List<HashMap<String, Object>>) tasks.get(TASK_NOTIFICATION_KEY));
                do_Task_Ads((List<HashMap<String, String>>) tasks.get(TASK_ADS_KEY));
                do_Task_Files_Download_And_Update((List<HashMap<String,Object>>) tasks.get(TASK_FILE_KEY));

                //TODO  header.yml File most Update at LAST after Downloading thumbnails

            }

            private String loadAdData() {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();

                try {
                    request.setURI(new URI(tasksFileUrl));
                } catch (URISyntaxException e) {
                    //Log.w("GOLVAZHE", "Failed to load ad! (URI)");
                    return null;
                }

                String content;

                try {
                    content = client.execute(request, new BasicResponseHandler());
                } catch (IOException e) {
                    //Log.w("GOLVAZHE", "Failed to load ad! (IO)");
                    return null;
                }

                return content;
            }

            private String getCurrentToken() {
                return preferences.getString("token", "null");
            }

            private void setToken(String token) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", token);
                editor.commit();
            }

            /**
             *
             * - URL: http://url/to/file
             *   name: file.frmt
             *   version: 1
             *
             */
            public void do_Task_Files_Download_And_Update(List<HashMap<String,Object>> files) {
                for(HashMap<String,Object> file : files) {
                    String url = (String) file.get("URL");
                    String name = (String) file.get("name");
                    int version = (Integer)file.get("version");
                    int lastVersion = preferences.getInt(name + "_VERSION", -1);
                    if(lastVersion == -1 || lastVersion > version) {
                        try {
                            Utils.download(context, url, name);
                            preferences.edit().putInt(name+"_VERSION",version).commit();
                        } catch (Exception e) {
//                        Log.d("synch","Error in DOWNLOADING File",e);
                            e.printStackTrace();
                        }
                    }
                }
            }

            /**
             *
             * - Image URL: http://url/to/image          //Notif_Image will be shown in notif bar and also ad_activity
             *   Title: notif title
             *   Text: notif text
             *   onClick: notif on Click
             *   Promote: notif Promote        OPTIONAL
             *   Prize: notif Prize            OPTIONAL
             *   min version: 4		           OPTIONAL
             *   max version: 5		           OPTIONAL
             *   token: 4			           OPTIONAL FOR IMPORTANT NOTIFS
             *   to token: 5			       OPTIONAL
             *
             */
            public void do_Task_Notifs(List<HashMap<String,Object>> notifs) {
                for(HashMap<String,Object> notif : notifs) {
                    int lastToken = preferences.getInt("TOKEN",-1);
                    if(notif.get("token") != null) {
                        int token = (Integer)notif.get("token");
                        if(token<lastToken) // old notification
                            return;
                        int toToken = (Integer)notif.get("to token");
                        preferences.edit().putInt("TOKEN",toToken).commit();
                    }
                    int minVersion = notif.get("min version")==null?-1000:(Integer)notif.get("min version");
                    int maxVersion = notif.get("max version")==null?+1000:(Integer)notif.get("max version");
                    //TODO check version
                    String imageUrl = (String) notif.get("Image URL");
                    String title = (String) notif.get("Title");
                    String text = (String) notif.get("Text");
                    String onClick = (String) notif.get("onClick");
                    String promote = (String) notif.get("Promote");
                    int prize = notif.get("Prize")==null?0:(Integer)notif.get("Prize");
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.tiny)
                                    .setContentTitle(title)
                                    .setContentText(text)
                                    .setAutoCancel(true);
                    String randomName = null;
                    if(imageUrl != null) {
                        try {
                            randomName = UUID.randomUUID().toString();
                            Utils.download(context, imageUrl, randomName+".jpg");
                            Bitmap bitmap = BitmapFactory.decodeStream(context.openFileInput(randomName));
                            mBuilder.setLargeIcon(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    sendNotification(mBuilder, onClick, promote, prize, randomName+".jpg");
                }
            }

            private void sendNotification(NotificationCompat.Builder mBuilder, String onclick, String promote, int prize,
                                          String imageName) {

                Intent resultIntent = new Intent(context, AdActivity.class);
                resultIntent.putExtra("onclick", onclick);
                resultIntent.putExtra("promote", promote);
                resultIntent.putExtra("prize", prize);
                resultIntent.putExtra("imageName", imageName);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

                stackBuilder.addParentStack(AdActivity.class);

                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                int mId = (int) (Math.random()*1000);
                mNotificationManager.notify(mId, mBuilder.build());
            }

            /**
             *
             * - URL: http://url/to/ad/image
             *   onClick: http://url/when/clicked
             *
             */
            public void do_Task_Ads(List<HashMap<String,String>> ads) {
                int cnt=0;
                String[] onClicks = new String[ads.size()];
                for(HashMap<String,String> ad : ads) {
                    String url = ad.get("URL");
                    onClicks[cnt] = ad.get("onClick");
                    try {
                        Utils.download(context, url, "ad" + cnt + ".jpg");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return; // to avoid broken synch
                    }
                    cnt++;
                }
                //successful synch hence rename tmp_ad to ad
                SharedPreferences.Editor editor = preferences.edit();
                for(int i=0; i<cnt; ++i) {
//                    File oldOne = new File(context.getFilesDir(),"tmp_ad"+i);
//                    File newOne = new File(context.getFilesDir(),"ad"+i);
//                    oldOne.renameTo(newOne);
//                    try {
//                        InputStream fis = context.openFileInput("tmp_ad" + i+ ".jpg");
//                        FileOutputStream fos = context.openFileOutput("ad" + i + ".jpg", 0);
////                        FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), "ad" + i));
//                        Utils.pipe(fis, fos);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    editor.putString("ad_onClick" + i, onClicks[i]);
                }

                editor.putInt(AdItemAdapter.ADS_KEY, cnt);
                editor.commit();
            }
        });

        thread.start();

    }
}
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

    private static final String tasksFileUrl = "http://192.168.1.5/tasks.yml";
    private final String PREFS_TAG = "ad_data";
    private final static String AD_UPDATE_TAG = "last_ad_update";
    private static boolean firstConnect = true;
    private HashMap<String, Object> tasks;
    private static final String TASK_FILE_KEY="Files",
                                TASK_NOTIFICATION_KEY="Notifications",
                                TASK_ADS_KEY="Ads";


    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("synch","onRecieve");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null || activeNetwork.getType() != ConnectivityManager.TYPE_WIFI || !activeNetwork.isConnected()) {
            firstConnect = true;
            return;
        }

        if (!firstConnect)
            return;
        firstConnect = false;

        Log.d("synch","passed network state ifs");

        final SharedPreferences preferences = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("synch","onThread run method");
                String lastTime = preferences.getString(AD_UPDATE_TAG, "2000-01-01");
                String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(AD_UPDATE_TAG, nowTime);
                    editor.commit();
                }
                //TODO uncomment below
//                if (lastTime.equals(nowTime)) return;
//                String data = loadAdData();
                String data = "Token: this is new token\n" +
                        "Files:\n" +
                        "  - URL: http://192.168.1.5/hello.yml\n" +
                        "    name: hello.yml\n" +
                        "Notifications:\n" +
                        "  - Image URL: http://192.168.1.5/notif.jpg\n" +
                        "    Title: This is Title\n" +
                        "    Text: This is Text\n" +
                        "    onClick: this is onClick\n" +
                        "    Promote: London baby!\n" +
                        "    Prize: 1243\n" +
                        "  - Image URL: http://192.168.1.5/notif.jpg\n" +
                        "    Title: This is Title\n" +
                        "    Text: This is Text\n" +
                        "    onClick: this is onClick\n" +
                        "    Promote: London baby!\n" +
                        "    Prize: 1243\n" +
                        "Ads:\n" +
                        "  - URL: http://192.168.1.5/aftabe.jpg\n" +
                        "  - URL: http://192.168.1.5/tantak.jpg";
                Log.d("synch","data received " + data);

                if (data == null)
                    return;
                String token;

                //download task File
                Yaml yaml = new Yaml();
                tasks = (HashMap<String, Object>) yaml.load(data);

                token = (String) tasks.get("Token");
                Log.d("synch","token read: " + token);

                Log.d("synch","doing File tasks");
                do_Task_Files_Download_And_Update((List<HashMap<String,String>>) tasks.get(TASK_FILE_KEY));
                Log.d("synch","doing notif tasks");
                do_Task_Notifs((List<HashMap<String, String>>) tasks.get(TASK_NOTIFICATION_KEY));
                Log.d("synch","doing ads tasks");
                do_Task_Ads((List<HashMap<String, String>>) tasks.get(TASK_ADS_KEY));

                //TODO uncomment below
//                setToken(token);
            }

            private String loadAdData() {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();

                try {
                    request.setURI(new URI(tasksFileUrl + getCurrentToken()));
                    Log.d("synch","requesting data from: "+tasksFileUrl + getCurrentToken());
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
            public void do_Task_Files_Download_And_Update(List<HashMap<String,String>> files) {
                for(HashMap<String,String> file : files) {
                    String url = file.get("URL");
                    String name = file.get("name");
                    Log.d("synch","A new File task " +name+" "+url);
                    try {
                        Utils.download(context,url,name);
                    } catch (Exception e) {
                        Log.d("synch","Error in downloading File",e);
                        e.printStackTrace();
                    }
                }
            }

            public void do_Task_Notifs(List<HashMap<String,String>> notifs) {
                for(HashMap<String,String> notif : notifs) {
                    String imageUrl = notif.get("Image URL");
                    String title = notif.get("Title");
                    String text = notif.get("Text");
                    String onClick = notif.get("onClick");
                    String promote = "hello"; //notif.get("Promote");
                    int prize = 2;//Integer.parseInt(notif.get("Prize"));
                    Log.d("synch","A notif task "+ title);
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
                            Utils.download(context, imageUrl, randomName);
                            Bitmap bitmap = BitmapFactory.decodeStream(context.openFileInput(randomName));
                            mBuilder.setLargeIcon(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    sendNotification(mBuilder, onClick, promote, prize, randomName);
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

                int mId = 1;
                mNotificationManager.notify(mId, mBuilder.build());
            }

            public void do_Task_Ads(List<HashMap<String,String>> ads) {
                int cnt=0;
                for(HashMap<String,String> ad : ads) {
                    String url = ad.get("URL");
                    Log.d("synch","An ad task "+ url);
                    try {
                        Utils.download(context,url,"ad"+cnt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cnt++;
                }

                SharedPreferences preferences = context.getSharedPreferences(Utils.sharedPrefrencesTag(), context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(AdItemAdapter.ADS_KEY,cnt);
                editor.commit();
            }
        });

        thread.start();

    }
}

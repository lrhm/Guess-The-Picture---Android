package ir.treeco.aftabe.synchronization;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import ir.treeco.aftabe.Adapter.AdItemAdapter;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.utils.NotificationBuilder;
import ir.treeco.aftabe.utils.Utils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hossein on 8/18/14.
 */
public class Synchronizer extends BroadcastReceiver{

    private static final String tasksFileUrl = "http://static.treeco.ir/packages/tasks.yml";
    private final static String AD_UPDATE_TAG = "last_ad_update";
    private HashMap<String, Object> tasks;
    private static final String TASK_FILE_KEY="Files",
                                TASK_NOTIFICATION_KEY="Notifications",
                                TASK_ADS_KEY="Ads";
    static private PackageManager packageManager;

    static public void setPackageManager(PackageManager pManager) {
        packageManager = pManager;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("synch","onRecieve");
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null /*|| activeNetwork.getType() != ConnectivityManager.TYPE_WIFI*/ || !activeNetwork.isConnected()) {
            return;
        }

        Log.d("synch","passed network state ifs");

        final SharedPreferences preferences = context.getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        Thread thread = new Thread(new Runnable() {
            boolean success = true;
            @Override
            public void run() {
                String lastTime = preferences.getString(AD_UPDATE_TAG, "2000-01-01");
                String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                if (lastTime.equals(nowTime)) return;

                String data = loadAdData();

                if (data == null)
                    return;

                //download task File
                Yaml yaml = new Yaml();
                tasks = (HashMap<String, Object>) yaml.load(data);

                do_Task_Notifs((List<HashMap<String, Object>>) tasks.get(TASK_NOTIFICATION_KEY));
                Log.d("synch","after notifs");
                do_Task_Ads((List<HashMap<String, String>>) tasks.get(TASK_ADS_KEY));
                Log.d("synch","after ads");
                do_Task_Files_Download_And_Update((List<HashMap<String,Object>>) tasks.get(TASK_FILE_KEY));
                Log.d("synch","after files");

                if (packageManager != null)
                    packageManager.refresh();

                if (success) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(AD_UPDATE_TAG, nowTime);
                    editor.commit();
                }
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
                    Log.d("synch","dling file: "+name);
                    int version = (Integer)file.get("version");
                    int lastVersion = preferences.getInt(name + "_VERSION", -1);
                    if(lastVersion == -1 || lastVersion < version || version == -1) {
                        try {
                            Utils.download(context, url, name);
                            preferences.edit().putInt(name+"_VERSION",version).commit();
                        } catch (Exception e) {
//                        Log.d("synch","Error in DOWNLOADING File",e);
                            e.printStackTrace();
                            success = false;
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
                int dayOffset = 1;
                for(HashMap<String,Object> notif : notifs) {
                    Log.d("synch","notifing");
                    int minVersion = notif.get("min version")==null?-1000:(Integer)notif.get("min version");
                    int maxVersion = notif.get("max version")==null?+1000:(Integer)notif.get("max version");
                    int version = 1;
                    try {
                        version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
                    } catch (android.content.pm.PackageManager.NameNotFoundException e) {
                        // Never gonna happen
                        e.printStackTrace();
                    }
                    if(version < minVersion || version > maxVersion)
                        continue;
                    int lastToken = preferences.getInt("TOKEN",-1);
                    if(notif.get("token") != null) {
                        int token = (Integer)notif.get("token");
                        if(token<lastToken) // expired notification
                            continue;
                    }

                    String time = (String) notif.get("time");
                    String[] timeHourMinute = time.split(":");
                    int hour = Integer.parseInt(timeHourMinute[0]);
                    int minute = Integer.parseInt(timeHourMinute[1]);

                    Intent intent = new Intent(context, NotificationBuilder.class);
                    intent.putExtra("title" ,(String) notif.get("Title"));
                    intent.putExtra("text" , (String) notif.get("Text"));
                    intent.putExtra("onClick" , (String) notif.get("onClick"));
                    intent.putExtra("promote", (String) notif.get("Promote"));
                    intent.putExtra("prize", notif.get("Prize")==null?0:(Integer)notif.get("Prize"));
                    String randomName = "NOTIFIMAGE_" + ((int) Math.random()*10000)+".jpg";
                    try {
                        Utils.download(context, (String) notif.get("Image URL"), randomName);
                    } catch (Exception e) {
                        success = false;
                        e.printStackTrace();
                    }
                    intent.putExtra("imageName", randomName);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent, 0);

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, dayOffset);
                    dayOffset++;
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

                    int toToken = (Integer)notif.get("to token");
                    preferences.edit().putInt("TOKEN",toToken).commit();
                }
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
                        success = false;
                        e.printStackTrace();
                    }
                    cnt++;
                }
                SharedPreferences.Editor editor = preferences.edit();
                for(int i=0; i<cnt; ++i) {
                    editor.putString("ad_onClick" + i, onClicks[i]);
                }

                editor.putInt(AdItemAdapter.ADS_KEY, cnt);
                editor.commit();
            }
        });

        thread.start();

    }
}
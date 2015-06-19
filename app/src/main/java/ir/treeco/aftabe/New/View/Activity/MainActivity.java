package ir.treeco.aftabe.New.View.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.BackgroundDrawable;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.Util.Zip;
import ir.treeco.aftabe.New.View.Fragment.PackageFragmentNew;
import ir.treeco.aftabe.R;

public class MainActivity extends FragmentActivity {
    public final static String FRAGMENT_TYPE = "fragment_type";
    private FragmentPagerItemAdapter fragmentPagerItemAdapter;
    private ViewPager viewPager;
    private HeadObject headObject;
    //NotificationAdapter notificationAdapter;
    Context context;
//    SharedPreferences preferences;
    // public static HeadObject downlodedObject; //todo in bayad static class she ehtemalan


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

//        preferences = getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);

        // Initialize Height Manager
//        LengthManager.initialize(this); //todo delete

        // set main activity background
        setOriginalBackgroundColor();

        context = this;
        loadDownloadedObject();
        headObject = new HeadObject();
        parseJson();

        //region Setup fragments in ViewPager
        fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("تازه‌ها", PackageFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 0).get())
                .add("دانلود شده‌ها", PackageFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 1).get())
                .add("محبوب‌ترین‌ها", PackageFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 2).get())
                .create()
        );
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerItemAdapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        //endregion

        //region Downloads
        DLManager.getInstance(this).dlStart("http://rsdn.ir/files/aftabe/head.json", this.getFilesDir().getPath(), //todo in hamishe nabayad ejra she
                new DLTaskListener() {
                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        parseJson();
                        downloadTask();
                    }
                }
        );
        //endregion
    }

    public void parseJson() {
        try {
            String a = this.getFilesDir().getPath() + "/head.json";
            Log.e("path", a);
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            headObject = gson.fromJson(reader, HeadObject.class);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void downloadTask() {
        for (int i = 0; i < headObject.getDownloadtask().size(); i++) {
            File file = new File(this.getFilesDir().getPath() + "/" + headObject.getDownloadtask().get(i).getName());
            if (!file.exists()) {
                DLManager.getInstance(this).dlStart(headObject.getDownloadtask().get(i).getUrl(), this.getFilesDir().getPath(),
                        new DLTaskListener() {

                            @Override
                            public void onFinish(File file) {
                                super.onFinish(file);
                                Log.e("don", file.getPath());
                            }
                        }
                );
            }
        }
    }

    public HeadObject getHeadObject() {
        return headObject;
    }

    public void setHeadObject(HeadObject headObject) {
        this.headObject = headObject;
    }

    public void downloadPackage(String url, String path, final int id, final String name) {

        //notificationAdapter = new NotificationAdapter(id, this, name);
        DLManager.getInstance(this).dlStart(url, path, new DLTaskListener() {
                    int n = 0;

                    @Override
                    public void onProgress(int progress) {
                        super.onProgress(progress);

                        n++;
                        if (n == 30) {
                            //notificationAdapter.notifyDownload(progress, id, name);
                            n = 0;

                            Log.e("don", "progress" + progress);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        //notificationAdapter.faildDownload(id, name);

                        Log.e("don", "error");
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file); //todo chack md5 & save in json file
                        //notificationAdapter.finalDownload(id, name);
                        Log.e("don", "finish " + file.getPath());
                        Zip zip = new Zip();
                        zip.unpackZip(file.getPath(), id, context);
                        saveToDownloads(id);
                    }
                }
        );
    }

    public void saveToDownloads(int id) {
        PackageObject packageObject = null;

        try {
            String a = this.getFilesDir().getPath() + "/Downloaded/" + id + "_level_list.json";
            Log.e("path", a);
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            packageObject = gson.fromJson(reader, PackageObject.class);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (MainApplication.downloadedObject.getDownloaded() == null) {
            ArrayList<PackageObject> a = new ArrayList<>();
            MainApplication.downloadedObject.setDownloaded(a);
        }

        MainApplication.downloadedObject.getDownloaded().add(packageObject);
        MainApplication.saveDataAndBackUpData(this);

    }

    public void loadDownloadedObject() {
        try {
            String downloadedPAth = this.getFilesDir().getPath() + "/downloaded.json";
            Log.e("downloadedPAth", downloadedPAth);

            File file = new File(downloadedPAth);
            if (file.exists()) {
                InputStream downloadedPAthinputStream = new FileInputStream(downloadedPAth);
                Reader readerd = new InputStreamReader(downloadedPAthinputStream, "UTF-8");
                Gson gsond = new GsonBuilder().create();
                MainApplication.downloadedObject = gsond.fromJson(readerd, HeadObject.class);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void saveData(){
        String aa = this.getFilesDir().getPath() + "/downloaded.json";

        Gson gson = new Gson();
        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = gson.toJson(downlodedObject);

        File file = new File(aa);
        file.delete();

        try {
            String backUpData = "/data/Android System/file.json";
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(backUpData);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("armin onPause", this.getClass().toString() + " is on Pause and we save data");
        MainApplication.saveDataAndBackUpData(this);
    }

    //region SetBackGroundDrawable
    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#F3C81D"),
                Color.parseColor("#F3C01E"),
                Color.parseColor("#F49C14")
        }));
    }
    //endregion
}

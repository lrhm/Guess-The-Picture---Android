package ir.treeco.aftabe.New.View.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.New.Adapter.NotificationAdapter;
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
    NotificationAdapter notificationAdapter;
    Context context;
    public static HeadObject downlodedObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

        context = this;
        loadDownloadedObject();
        headObject = new HeadObject();
        parseJson();

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

        DLManager.getInstance(this).dlStart("http://rsdn.ir/files/aftabe/head.json", this.getFilesDir().getPath(),
                new DLTaskListener() {
                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        parseJson();
                        downloadTask();
                    }
                }
        );
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

    public void downloadPackage(String url, String path, final int id, final String name){

        notificationAdapter = new NotificationAdapter(id, this, name);
        DLManager.getInstance(this).dlStart(url, path, new DLTaskListener() {
                    int n = 0;
                    @Override
                    public void onProgress(int progress) {
                        super.onProgress(progress);

                        n++;
                        if (n == 30) {
                            notificationAdapter.notifyDownload(progress, id, name);
                            n = 0;

                            Log.e("don", "progress" + progress);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        notificationAdapter.faildDownload(id, name);

                        Log.e("don", "error");
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file); //todo chack md5 & save in json file
                        notificationAdapter.finalDownload(id, name);
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

        if (downlodedObject==null){
            downlodedObject = new HeadObject();
        }

        if (downlodedObject.getDownloaded()==null){
            ArrayList<PackageObject> a = new ArrayList<>();
            downlodedObject.setDownloaded(a);
        }

        downlodedObject.getDownloaded().add(packageObject);
        String aa = this.getFilesDir().getPath() + "/downloaded.json";

        Gson gson = new Gson();
        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = gson.toJson(downlodedObject);
        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(aa);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDownloadedObject() {
        try {
            String downloadedPAth = this.getFilesDir().getPath() + "/downloaded.json";
            Log.e("downloadedPAth", downloadedPAth);

            File file = new File(downloadedPAth);
            if(file.exists()) {
                InputStream downloadedPAthinputStream = new FileInputStream(downloadedPAth);
                Reader readerd = new InputStreamReader(downloadedPAthinputStream, "UTF-8");
                Gson gsond = new GsonBuilder().create();
                downlodedObject = gsond.fromJson(readerd, HeadObject.class);

//                file.delete();
            }

        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData(){
        String aa = this.getFilesDir().getPath() + "/downloaded.json";

        Gson gson = new Gson();
        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = gson.toJson(downlodedObject);
        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(aa);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package ir.treeco.aftabe.New.View.Activity;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.View.Fragment.PackageFragmentNew;
import ir.treeco.aftabe.R;

public class MainActivity extends FragmentActivity {
    public final static String FRAGMENT_TYPE = "fragment_type";
    private FragmentPagerItemAdapter fragmentPagerItemAdapter;
    private ViewPager viewPager;
    private HeadObject headObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

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
                });
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
        for (int i = 0; i < headObject.getDownloadtask().length; i++) {
            File file = new File(this.getFilesDir().getPath() + "/" + headObject.getDownloadtask()[i].getName());
            if (!file.exists()) {
                DLManager.getInstance(this).dlStart(headObject.getDownloadtask()[i].getUrl(), this.getFilesDir().getPath(),
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

    public void downloadPackage(String url, String path){
        DLManager.getInstance(this).dlStart(url, path, new DLTaskListener() {
                    @Override
                    public void onProgress(int progress) {
                        super.onProgress(progress);
                        Log.e("don", "progress" + progress);
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);

                        Log.e("don", "error");
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        Log.e("don", "finish "+file.getPath());
                    }
                }
        );
    }
}

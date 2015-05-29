package ir.treeco.aftabe.New.View.Activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.File;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.New.View.Fragment.PackageFragmentNew;
import ir.treeco.aftabe.R;

public class MainActivity extends FragmentActivity {
    public final static String FRAGMENT_TYPE = "fragment_type";
    private FragmentPagerItemAdapter fragmentPagerItemAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

        fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("تازه‌ها", PackageFragmentNew.class, new Bundler().putString(FRAGMENT_TYPE, "new").get())
                .add("دانلود شده‌ها", PackageFragmentNew.class, new Bundler().putString(FRAGMENT_TYPE, "download").get())
                .add("محبوب‌ترین‌ها", PackageFragmentNew.class, new Bundler().putString(FRAGMENT_TYPE, "popular").get())
                .create()
        );


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerItemAdapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        DLManager.getInstance(this).dlStart("http://rsdn.ir/files/aftabe/head.json", this.getFilesDir().getPath(),
                new DLTaskListener() {

                    @Override
                    public boolean onConnect(int type, String msg) {
                        return super.onConnect(type, msg);
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        PackageFragmentNew fragment0 = (PackageFragmentNew) getSupportFragmentManager().getFragments().get(0);
                        fragment0.refresh();

                        PackageFragmentNew fragment2 = (PackageFragmentNew) getSupportFragmentManager().getFragments().get(2);
                        fragment2.refresh();
                    }
                });
    }

}

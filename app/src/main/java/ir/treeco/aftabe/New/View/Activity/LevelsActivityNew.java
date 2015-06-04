package ir.treeco.aftabe.New.View.Activity;

import android.content.Intent;
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.View.Fragment.LevelsFragmentNew;
import ir.treeco.aftabe.R;

public class LevelsActivityNew extends FragmentActivity {
    int packageId;
    PackageObject packageObject;
    public final static String LEVEL_PAGE = "level_page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        packageId = intent.getIntExtra("id", 0);
        setContentView(R.layout.new_activity_levels);

        parseJson();

        int pagesize = packageObject.getLevels().size() / 16;
        if ((pagesize % 16) != 0) {
            pagesize ++;
        }

        FragmentPagerItems.Creator fragmentPagerItemsCreator = FragmentPagerItems.with(this);
        for (int i = 0; i < pagesize; i++) {
            fragmentPagerItemsCreator.add("", LevelsFragmentNew.class, new Bundler().putInt(LEVEL_PAGE, i).get());
        }

        FragmentPagerItems fragmentPagerItems = fragmentPagerItemsCreator.create();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), fragmentPagerItems);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }

    public void parseJson() {
        try {
            String downloadedPAth = this.getFilesDir().getPath() + "/downloaded.json";
            Log.e("downloadedPAth", downloadedPAth);

            InputStream downloadedPAthinputStream = new FileInputStream(downloadedPAth);
            Reader readerd = new InputStreamReader(downloadedPAthinputStream, "UTF-8");
            Gson gsond = new GsonBuilder().create();
            HeadObject downlodedObject = gsond.fromJson(readerd, HeadObject.class);

            for (int i = 0; downlodedObject.getDownloaded().size() > i; i++) {
                if (downlodedObject.getDownloaded().get(i).getId() == packageId) {
                    packageObject = downlodedObject.getDownloaded().get(i);
                    break;
                }
            }

        } catch ( Exception e) {
            e.printStackTrace();
        }
    }



    public PackageObject getPackageObject() {
        return packageObject;
    }
}

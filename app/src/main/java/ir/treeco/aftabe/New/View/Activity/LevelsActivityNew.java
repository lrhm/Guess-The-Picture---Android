package ir.treeco.aftabe.New.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe.New.View.Fragment.LevelsFragmentNew;
import ir.treeco.aftabe.R;

public class LevelsActivityNew extends FragmentActivity {
    int packageId;
//    PackageObject packageObject;
    public final static String LEVEL_PAGE = "level_page";
    public final static String PACKAGE_NUMBER = "package_number";
    int packageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        packageId = intent.getIntExtra("id", 0);
        setContentView(R.layout.new_activity_levels);

        parseJson();

        int pagesize = MainActivity.downlodedObject.getDownloaded().get(packageNumber).getLevels().size() / 16;
        if ((pagesize % 16) != 0) {
            pagesize ++;
        }

        FragmentPagerItems.Creator fragmentPagerItemsCreator = FragmentPagerItems.with(this);
        for (int i = 0; i < pagesize; i++) {
            fragmentPagerItemsCreator.add("", LevelsFragmentNew.class, new Bundler().putInt(LEVEL_PAGE, i).putInt(PACKAGE_NUMBER, packageNumber).get());
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

            for (int i = 0; MainActivity.downlodedObject.getDownloaded().size() > i; i++) {
                if (MainActivity.downlodedObject.getDownloaded().get(i).getId() == packageId) {
                    packageNumber = i;
//                    packageObject = MainActivity.downlodedObject.getDownloaded().get(i);
                    break;
                }
            }

        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

//    public PackageObject getPackageObject() {
//        return packageObject;
//    }
}

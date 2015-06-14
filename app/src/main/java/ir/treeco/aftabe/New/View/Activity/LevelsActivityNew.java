package ir.treeco.aftabe.New.View.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe.BackgroundDrawable;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.View.Fragment.LevelsFragmentNew;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.Utils;

public class LevelsActivityNew extends FragmentActivity {
    int packageId;
    //    PackageObject packageObject;
    public final static String LEVEL_PAGE = "level_page";
    public final static String PACKAGE_NUMBER = "package_number";
    int packageNumber;

//    public Bitmap levelLocked;
//    public Bitmap levelUnlocked;
//
//    float[] thumbnailHSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        packageId = intent.getIntExtra("id", 0);
        setContentView(R.layout.new_activity_levels);

//        thumbnailHSV = new float[] {Float.parseFloat("130"),Float.parseFloat("0"),Float.parseFloat("-0.1")};
//
//        levelLocked = Utils.updateHSV(ImageManager
//                        .loadImageFromResource(this, R.drawable.level_locked,
//                                LengthManager.getLevelFrameWidth(),
//                                LengthManager.getLevelFrameHeight()),
//                thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);
//
//        levelUnlocked = Utils.updateHSV(ImageManager
//                        .loadImageFromResource(this, R.drawable.level_unlocked,
//                                LengthManager.getLevelFrameWidth(),
//                                LengthManager.getLevelFrameHeight()),
//                thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);


        // set background drawable
        setOriginalBackgroundColor();

        // set top and bottom curved bar
        setTopAndBottomBar();

        parseJson();

        int pagesize = MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().size() / 16;
        if ((MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().size() % 16) != 0) {
            pagesize++;
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


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("armin onPause", this.getClass().toString() + " is on Pause and we save data");
        MainApplication.saveDataAndBackUpData(this);
    }

    public void parseJson() {
        try {

            for (int i = 0; MainApplication.downloadedObject.getDownloaded().size() > i; i++) {
                if (MainApplication.downloadedObject.getDownloaded().get(i).getId() == packageId) {
                    packageNumber = i;
//                    packageObject = MainActivity.downlodedObject.getDownloaded().get(i);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //region SetTopAndBottomBar
    public void setTopAndBottomBar() {
        ImageView levelsBackTop = (ImageView) findViewById(R.id.levels_back_top);
        levelsBackTop.setImageBitmap(ImageManager.loadImageFromResource(LevelsActivityNew.this, R.drawable.levels_back_top, LengthManager.getScreenWidth(), LengthManager.getLevelsBackTopHeight()));
        Utils.resizeView(levelsBackTop, LengthManager.getScreenWidth(), LengthManager.getLevelsBackTopHeight());
        ImageView levelsBackBottom = (ImageView) findViewById(R.id.levels_back_bottom);
        levelsBackBottom.setImageBitmap(ImageManager.loadImageFromResource(LevelsActivityNew.this, R.drawable.levels_back_bottom, LengthManager.getScreenWidth(), LengthManager.getLevelsBackBottomHeight()));
        Utils.resizeView(levelsBackBottom, LengthManager.getScreenWidth(), LengthManager.getLevelsBackBottomHeight());
    }
    //endregion

    //region SetBackGroundDrawable
    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#29CDB8"),
                Color.parseColor("#1FB8AA"),
                Color.parseColor("#0A8A8C")
        }));
    }
    //endregion

//    public PackageObject getPackageObject() {
//        return packageObject;
//    }
}

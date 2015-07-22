package ir.treeco.aftabe.New.View.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.New.Util.Tools;
import ir.treeco.aftabe.R;

public class LevelsActivityNew extends Fragment {
    int packageId;
    //    PackageObject packageObject;
    public final static String LEVEL_PAGE = "level_page";
    public final static String PACKAGE_NUMBER = "package_number";
    int packageNumber;

    public Bitmap levelLocked;
    public Bitmap levelUnlocked;
//
//    float[] thumbnailHSV;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_activity_levels, container, false);

        super.onCreate(savedInstanceState);

        packageId = getArguments().getInt("id");

        float[] thumbnailHSV = {1, 1, 1};  //todo
        levelLocked = Tools.updateHSV(ImageManager.loadImageFromResource(getActivity(), R.drawable.level_locked, MainApplication.lengthManager.getLevelFrameWidth(), MainApplication.lengthManager.getLevelFrameHeight()), thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);
        levelUnlocked = Tools.updateHSV(ImageManager.loadImageFromResource(getActivity(), R.drawable.level_unlocked, MainApplication.lengthManager.getLevelFrameWidth(), MainApplication.lengthManager.getLevelFrameHeight()), thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);



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

        // set top and bottom curved bar
//        setTopAndBottomBar();

        int pagesize = MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().size() / 16;
        if ((MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().size() % 16) != 0) {
            pagesize++;
        }

        FragmentPagerItems.Creator fragmentPagerItemsCreator = FragmentPagerItems.with(getActivity());
        for (int i = 0; i < pagesize; i++) {
            fragmentPagerItemsCreator.add("", LevelsFragmentNew.class, new Bundler().putInt(LEVEL_PAGE, i).putInt(PACKAGE_NUMBER, packageNumber).get());
        }

        FragmentPagerItems fragmentPagerItems = fragmentPagerItemsCreator.create();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), fragmentPagerItems);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

    return view;
    }

    public Bitmap getLevelLocked() {
        return levelLocked;
    }

    public Bitmap getLevelUnlocked() {
        return levelUnlocked;
    }
}

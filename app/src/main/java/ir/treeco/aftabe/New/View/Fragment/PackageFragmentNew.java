package ir.treeco.aftabe.New.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Adapter.DBAdapter;
import ir.treeco.aftabe.New.Object.Level;
import ir.treeco.aftabe.New.Util.Tools;
import ir.treeco.aftabe.R;

public class PackageFragmentNew extends Fragment {
    public final static String LEVEL_PAGE = "level_page";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_activity_levels, container, false);
        super.onCreate(savedInstanceState);

        Tools tools = new Tools();
        DBAdapter db = DBAdapter.getInstance(getActivity());
        int packageId = getArguments().getInt("id");

        // set top and bottom curved bar
//        setTopAndBottomBar();

        ImageView levelsBackTop = (ImageView) view.findViewById(R.id.levels_back_top);

        levelsBackTop.setImageBitmap(MainApplication.imageManager.loadImageFromResource(
                R.drawable.levels_back_top,
                MainApplication.lengthManager.getScreenWidth(),
                MainApplication.lengthManager.getLevelsBackTopHeight()));

        tools.resizeView(levelsBackTop,
                MainApplication.lengthManager.getScreenWidth(),
                MainApplication.lengthManager.getLevelsBackTopHeight());

        ImageView levelsBackBottom = (ImageView) view.findViewById(R.id.levels_back_bottom);

        levelsBackBottom.setImageBitmap(MainApplication.imageManager.loadImageFromResource(
                R.drawable.levels_back_bottom,
                MainApplication.lengthManager.getScreenWidth(),
                MainApplication.lengthManager.getLevelsBackBottomHeight()));

        tools.resizeView(levelsBackBottom,
                MainApplication.lengthManager.getScreenWidth(),
                MainApplication.lengthManager.getLevelsBackBottomHeight());

        Level[] levels = db.getLevels(packageId);
        int pageSize = levels.length / 16;
        if ((levels.length % 16) != 0) {
            pageSize++;
        }

        FragmentPagerItems.Creator fragmentPagerItemsCreator = FragmentPagerItems.with(getActivity());
        for (int i = 0; i < pageSize; i++) {
            fragmentPagerItemsCreator.add("", LevelsFragmentNew.class,
                    new Bundler().putInt(LEVEL_PAGE, i).putInt("id", packageId).get());
        }

        FragmentPagerItems fragmentPagerItems = fragmentPagerItemsCreator.create();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), fragmentPagerItems);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tools.resizeView(viewPager, MainApplication.lengthManager.getScreenWidth(),
                MainApplication.lengthManager.getLevelsViewpagerHeight());

        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        return view;
    }
}

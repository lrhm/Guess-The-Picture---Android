package ir.treeco.aftabe2.View.Fragment;

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

import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Adapter.DBAdapter;
import ir.treeco.aftabe2.Object.Level;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.R;

public class PackageFragment extends Fragment {
    public final static String LEVEL_PAGE = "level_page";
    private static final String TAG = "PackageFragment";
    private LengthManager lengthManager;
    private ImageManager imageManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_levels, container, false);
        super.onCreate(savedInstanceState);

        lengthManager = ((MainApplication) getActivity().getApplication()).getLengthManager();
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        Tools tools = new Tools(getActivity());
        DBAdapter db = DBAdapter.getInstance(getActivity());
        int packageId = getArguments().getInt("id");
        ImageView levelsBackTop = (ImageView) view.findViewById(R.id.levels_back_top);

        levelsBackTop.setImageBitmap(imageManager.loadImageFromResource(
                R.drawable.levels_back_top,
                lengthManager.getScreenWidth(),
                lengthManager.getLevelsBackTopHeight()));

        tools.resizeView(levelsBackTop,
                lengthManager.getScreenWidth(),
                lengthManager.getLevelsBackTopHeight());

        ImageView levelsBackBottom = (ImageView) view.findViewById(R.id.levels_back_bottom);

        levelsBackBottom.setImageBitmap(imageManager.loadImageFromResource(
                R.drawable.levels_back_bottom,
                lengthManager.getScreenWidth(),
                lengthManager.getLevelsBackBottomHeight()));

        tools.resizeView(levelsBackBottom,
                lengthManager.getScreenWidth(),
                lengthManager.getLevelsBackBottomHeight());

        Level[] levels = db.getLevels(packageId);
        int pageSize = levels.length / 16;
        if ((levels.length % 16) != 0) {
            pageSize++;
        }

        FragmentPagerItems.Creator fragmentPagerItemsCreator = FragmentPagerItems.with(getActivity());
        for (int i = 0; i < pageSize; i++) {
            fragmentPagerItemsCreator.add("", LevelsFragment.class,
                    new Bundler().putInt(LEVEL_PAGE, i).putInt("id", packageId).get());
        }

        FragmentPagerItems fragmentPagerItems = fragmentPagerItemsCreator.create();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), fragmentPagerItems);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tools.resizeView(viewPager, lengthManager.getScreenWidth(),
                lengthManager.getLevelsViewpagerHeight());

        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);



        return view;
    }

}

package ir.treeco.aftabe.New.View.Fragment;

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

import ir.treeco.aftabe.New.Adapter.AdItemAdapter;
import ir.treeco.aftabe.New.View.Custom.AutoScrollViewPager;
import ir.treeco.aftabe.R;

public class MainFragment extends Fragment {
    private FragmentPagerItemAdapter fragmentPagerItemAdapter;
    private ViewPager viewPager;
    public final static String FRAGMENT_TYPE = "fragment_type";
    private AutoScrollViewPager autoScrollViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_main, container, false);

        autoScrollViewPager = (AutoScrollViewPager) view.findViewById(R.id.ad_view_pager);

        fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("تازه‌ها", PackagesFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 0).get())
                .add("دانلود شده‌ها", PackagesFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 1).get())
                .add("محبوب‌ترین‌ها", PackagesFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 2).get())
                .create()
        );

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerItemAdapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        setUpAds(autoScrollViewPager);

        return view;
    }

    private void setUpAds(AutoScrollViewPager autoScrollViewPager) {
        AdItemAdapter adItemAdapter = new AdItemAdapter(getActivity());
        autoScrollViewPager.setAdapter(adItemAdapter);
        autoScrollViewPager.setOffscreenPageLimit(1);
        autoScrollViewPager.setInterval(5000);
        autoScrollViewPager.startAutoScroll();
    }
}

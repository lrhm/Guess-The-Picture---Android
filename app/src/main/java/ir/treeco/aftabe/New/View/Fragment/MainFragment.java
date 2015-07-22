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

import ir.treeco.aftabe.R;

public class MainFragment extends Fragment {
    private FragmentPagerItemAdapter fragmentPagerItemAdapter;
    private ViewPager viewPager;
    public final static String FRAGMENT_TYPE = "fragment_type";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_main, container, false);


        fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("تازه‌ها", PackageFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 0).get())
                .add("دانلود شده‌ها", PackageFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 1).get())
                .add("محبوب‌ترین‌ها", PackageFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 2).get())
                .create()
        );


        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerItemAdapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        //endregion

        return view;
    }
}

package ir.treeco.aftabe.New.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe.New.Fragment.LevelsFragmentNew;
import ir.treeco.aftabe.R;

public class LevelsActivityNew extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_levels_activity);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("", LevelsFragmentNew.class)
                .add("", LevelsFragmentNew.class)
                .add("", LevelsFragmentNew.class)
                .add("", LevelsFragmentNew.class)
                .add("", LevelsFragmentNew.class)
                .add("", LevelsFragmentNew.class)
                .add("", LevelsFragmentNew.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }
}

package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.VerticalViewPager;

/**
 * Created by al on 12/24/15.
 */
public class OnlineMenuFragment extends Fragment {

    private boolean isDegreeOfFreedomSetted = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_online_menu, container, false);

        final VerticalViewPager verticalViewPager = (VerticalViewPager) view.findViewById(R.id.fragment_online_menu_vertical_view_pager);

        FragmentPagerItems.Creator fragmentPagerItemsCreator = FragmentPagerItems.with(getActivity());
        for (int i = 0; i < 2; i++) {
            fragmentPagerItemsCreator.add("", LevelsFragment.class,
                    new Bundler().putInt("level_page", i).putInt("id", 0).get());
        }

        FragmentPagerItems fragmentPagerItems = fragmentPagerItemsCreator.create();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), fragmentPagerItems);
        verticalViewPager.setAdapter(adapter);

        final MainFragment mainFragment =
                ((MainActivity) getActivity()).mainFragment;

        if (mainFragment.myCoordinatorLayout.isAllowForScrool()) {
            Log.d("OnlineMenuFragment", "its allowed , setting degreeOfFreedom to 1");
            verticalViewPager.setDegreeOfFreedom(0.1f);
            mainFragment.mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (Math.abs(verticalOffset) == mainFragment.mCollapsingToolbarLayout.getHeight() && !isDegreeOfFreedomSetted) {
                        isDegreeOfFreedomSetted =true;
                        verticalViewPager.setDegreeOfFreedom(4);
                        Log.d("OnlineMenuFragment" , "Setting degrreoffredom to 2");
                    }


                }
            });
        }


        return view;

    }


}

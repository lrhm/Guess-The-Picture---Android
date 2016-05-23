package ir.treeco.aftabe2.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.MySmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.VerticalViewPager;

/**
 * Created by al on 12/24/15.
 */
public class OnlineMenuFragment extends Fragment implements MySmartTabLayout.OnTabClickListener {


    MainFragment mainFragment;
    VerticalViewPager verticalViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_online_menu, container, false);

        verticalViewPager = (VerticalViewPager) view.findViewById(R.id.fragment_online_menu_vertical_view_pager);

        FragmentPagerItems.Creator fragmentPagerItemsCreator = FragmentPagerItems.with(getActivity());
        fragmentPagerItemsCreator.add("", OnlinePrimaryPageFragment.class);
        fragmentPagerItemsCreator.add("", FriendListFragment.class);


        FragmentPagerItems fragmentPagerItems = fragmentPagerItemsCreator.create();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), fragmentPagerItems);
        verticalViewPager.setAdapter(adapter);
        mainFragment =
                ((MainActivity) getActivity()).mainFragment;

        if (mainFragment != null && mainFragment.mSmartTabLayout != null)
            mainFragment.mSmartTabLayout.setOnTabClickListener(this);

        verticalViewPager.setDegreeOfFreedom(4);


        return view;

    }


    @Override
    public void onTabClicked(int position) {

        if (position == 1 && verticalViewPager.getCurrentItem() == 1) {
            verticalViewPager.setCurrentItem(0, true);
        }

    }
}

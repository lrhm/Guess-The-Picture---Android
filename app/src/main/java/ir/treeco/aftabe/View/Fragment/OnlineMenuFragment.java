package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
public class OnlineMenuFragment extends Fragment  {


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


        verticalViewPager.setDegreeOfFreedom(4);




        return view;

    }


}

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
import ir.treeco.aftabe.View.Custom.MyCoordinatorLayout;
import ir.treeco.aftabe.View.Custom.VerticalViewPager;

/**
 * Created by al on 12/24/15.
 */
public class OnlineMenuFragment extends Fragment implements VerticalViewPager.CustomListener {

    private boolean isDegreeOfFreedomSetted = false;

    MainFragment mainFragment;
    VerticalViewPager verticalViewPager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        Log.d("OnlineMenuFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_online_menu, container, false);

          verticalViewPager = (VerticalViewPager) view.findViewById(R.id.fragment_online_menu_vertical_view_pager);

        FragmentPagerItems.Creator fragmentPagerItemsCreator = FragmentPagerItems.with(getActivity());
        fragmentPagerItemsCreator.add("", OnlinePrimaryPageFragment.class);
        for (int i = 0; i < 1; i++) {
            fragmentPagerItemsCreator.add("", LevelsFragment.class,
                    new Bundler().putInt("level_page", i).putInt("id", 0).get());
        }

        FragmentPagerItems fragmentPagerItems = fragmentPagerItemsCreator.create();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), fragmentPagerItems);
        verticalViewPager.setAdapter(adapter);

        verticalViewPager.setDegreeOfFreedom(0.01f);

        mainFragment =
                ((MainActivity) getActivity()).mainFragment;


//        verticalViewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mainFragment.mAppBarLayout.setExpanded(false , true);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        verticalViewPager.setOnTouchListener(null);
//
//                    }
//                }, 200);
//                return true;
//
//            }
//        });

//        if (mainFragment.myCoordinatorLayout.isAllowForScrool()) {
//            Log.d("OnlineMenuFragment", "its allowed , setting degreeOfFreedom to 1");
//            verticalViewPager.setDegreeOfFreedom(0.1f);
//            mainFragment.mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//                @Override
//                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                    if (Math.abs(verticalOffset) == mainFragment.mCollapsingToolbarLayout.getHeight() && !isDegreeOfFreedomSetted) {
//                        isDegreeOfFreedomSetted = true;
//                        verticalViewPager.setDegreeOfFreedom(4);
//                        Log.d("OnlineMenuFragment", "Setting degrreoffredom to 2");
//                    }
//
//
//                }
//            });
//        } else {
            verticalViewPager.setDegreeOfFreedom(4);
//        }


        verticalViewPager.setCustomListener(this);
//
//        verticalViewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                MyCoordinatorLayout.LayoutParams params = (MyCoordinatorLayout.LayoutParams) mainFragment.mAppBarLayout.getLayoutParams();
//
//                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//                if (behavior != null) {
//                    behavior.onNestedPreScroll(mainFragment.myCoordinatorLayout, mainFragment.mAppBarLayout, verticalViewPager, 0, 1, new int[2]);
////                    behavior.setTopAndBottomOffset(-200);
//                }
//                return false;
//            }
//        });
        return view;

    }

    @Override
    public boolean onScroll(int dy) {
        if(!mainFragment.myCoordinatorLayout.isAllowForScrool())
            return false;
        MyCoordinatorLayout.LayoutParams params = (MyCoordinatorLayout.LayoutParams) mainFragment.mAppBarLayout.getLayoutParams();

        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedPreScroll(mainFragment.myCoordinatorLayout, mainFragment.mAppBarLayout, verticalViewPager, 0, -dy, new int[2]);
        }
        return true;


    }

    @Override
    public void onActionUp() {

        if(!mainFragment.myCoordinatorLayout.isAllowForScrool())
            return;
        mainFragment.mAppBarLayout.setExpanded(false , true);
    }
}

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
import ir.treeco.aftabe2.Util.Logger;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.VerticalViewPager;
import ir.treeco.aftabe2.View.Dialog.DialogAdapter;

/**
 * Created by al on 12/24/15.
 */
public class OnlineMenuFragment extends Fragment implements MySmartTabLayout.OnTabClickListener, VerticalViewPager.OnPageChangeListener {


    MainFragment mainFragment;
    public VerticalViewPager verticalViewPager;
    FragmentPagerItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_online_menu, container, false);

        verticalViewPager = (VerticalViewPager) view.findViewById(R.id.fragment_online_menu_vertical_view_pager);

        FragmentPagerItems.Creator fragmentPagerItemsCreator = FragmentPagerItems.with(getActivity());
        fragmentPagerItemsCreator.add("", OnlinePrimaryPageFragment.class);
        fragmentPagerItemsCreator.add("", FriendListFragment.class);


        FragmentPagerItems fragmentPagerItems = fragmentPagerItemsCreator.create();
        adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), fragmentPagerItems);
        verticalViewPager.setAdapter(adapter);
        mainFragment =
                ((MainActivity) getActivity()).mainFragment;

        if (mainFragment != null && mainFragment.mSmartTabLayout != null)
            mainFragment.mSmartTabLayout.setOnTabClickListener(this);

        verticalViewPager.setDegreeOfFreedom(4);

        verticalViewPager.setOnPageChangeListener(this);


        return view;

    }

    public FriendListFragment getFriendListFragment() {
        FriendListFragment friendListFragment = (FriendListFragment) adapter.getPage(1);
        return friendListFragment;
    }


    @Override
    public void onTabClicked(int position) {

        if (position == 1 && verticalViewPager.getCurrentItem() == 1) {

            verticalViewPager.setCurrentItem(0, true);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {


        if (position == 1)
            DialogAdapter.makeTutorialDialog(getContext(), "تو این قسمت شما می تونین دوستاتون رو به یک چالش آفتابه ای دعوت کنید و با هم رقابت کنین .برای این کار از طریق سرچ یا لیست مخاطباتون دوستاتون رو پیدا کنید و برای آنها درخواست دوستی بفرستید.", "");

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

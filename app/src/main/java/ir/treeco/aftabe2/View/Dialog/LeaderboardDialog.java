package ir.treeco.aftabe2.View.Dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.MySmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.View.Custom.DialogDrawable;
import ir.treeco.aftabe2.View.Fragment.LeaderboardFragment;
import ir.treeco.aftabe2.View.Fragment.UserInfoFragment;

public class LeaderboardDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout((int) (0.8 * SizeManager.getScreenWidth()), (int) (0.7 * SizeManager.getScreenHeight()));
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_leaderboard, container);
        Tools tools = new Tools(getContext());
        // tab slider
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add( " "+ "پروفایل"+" ", UserInfoFragment.class)
                .add("برترین ها", LeaderboardFragment.class)
                .create()
        );



        MySmartTabLayout smartTabLayout = (MySmartTabLayout) view.findViewById(R.id.smart_tab_leaderboard);
        smartTabLayout.getLayoutParams().height = (int) (SizeManager.getScreenHeight() * 0.08);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager_leaderboard);

        viewPager.setAdapter(fragmentPagerAdapter);
        smartTabLayout.setViewPager(viewPager);

        LinearLayout mDataContainer = (LinearLayout) view.findViewById(R.id.leaderboard_dialog_container);
        LinearLayout.LayoutParams layoutParams = new
                LinearLayout.LayoutParams((int) (0.8 * SizeManager.getScreenWidth()), (int) (0.7 * SizeManager.getScreenHeight()));
        mDataContainer.setLayoutParams(layoutParams);
        view.setLayoutParams(layoutParams);
        viewPager.setLayoutParams(layoutParams);
        tools.setViewBackground(mDataContainer, new DialogDrawable(getContext()));

        return view;

    }

}


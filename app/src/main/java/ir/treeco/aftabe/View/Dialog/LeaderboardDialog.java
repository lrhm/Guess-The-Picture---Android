package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ogaclejapan.smarttablayout.MySmartTabLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Custom.DialogDrawable;
import ir.treeco.aftabe.View.Fragment.LeaderboardFragment;
import ir.treeco.aftabe.View.Fragment.OnlineMenuFragment;
import ir.treeco.aftabe.View.Fragment.PackagesFragment;
import ir.treeco.aftabe.View.Fragment.UserInfoFragment;

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
                .add("پروفایل", UserInfoFragment.class)
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


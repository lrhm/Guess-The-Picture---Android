package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ogaclejapan.smarttablayout.MySmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe.Adapter.AdItemAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.View.Activity.MainActivity;

public class MainFragment extends Fragment {

    private FragmentPagerItemAdapter fragmentPagerItemAdapter;
    private ViewPager viewPager;
    public final static String FRAGMENT_TYPE = "fragment_type";
    AdItemAdapter adItemAdapter;
    public AppBarLayout mAppBarLayout;
    public MySmartTabLayout mSmartTabLayout;
    private ImageManager imageManager;
    ImageView shaderImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        adItemAdapter = new AdItemAdapter(getActivity());
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("    " + "آفلاین" + "    ", PackagesFragment.class, new Bundler().putInt(FRAGMENT_TYPE, 1).get())
                .add("    " + "آنلاین" + "    ", OnlineMenuFragment.class)
                .create()
        );

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerItemAdapter);


        mSmartTabLayout = (MySmartTabLayout) view.findViewById(R.id.viewpagertab);
        mSmartTabLayout.setViewPager(viewPager);

        mSmartTabLayout.getLayoutParams().height = (int) (SizeManager.getScreenHeight() * 0.08);


        shaderImageView = (ImageView) view.findViewById(R.id.shadeview);
        SizeConverter shadeConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth(), 1857, 23);
        shaderImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.shade, shadeConverter.mWidth, shadeConverter.mHeight, ImageManager.ScalingLogic.FIT));
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) shaderImageView.getLayoutParams();
//        params.topMargin = (int) (SizeManager.getScreenHeight() * 0.08);


        return view;
    }


}

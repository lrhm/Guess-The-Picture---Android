package ir.treeco.aftabe;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ir.treeco.aftabe.packages.Package;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.LoadingManager;
import ir.treeco.aftabe.utils.Utils;

/**
 * Created by hossein on 8/31/14.
 */
public class PackageFragment extends Fragment {
    private Package mPackage;
    private Bitmap levelLocked;
    private Bitmap levelUnlocked;

    public static PackageFragment newInstance(Package mPackage) {
        PackageFragment packageFragment = new PackageFragment();
        packageFragment.mPackage = mPackage;
        return packageFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_package, container, false);

        float[] cheatButtonHSV = mPackage.meta.getCheatButtonHSV();
        levelLocked = Utils.updateHSV(ImageManager.loadImageFromResource(getActivity(), R.drawable.level_locked, LengthManager.getLevelFrameWidth(), LengthManager.getLevelFrameHeight()), cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);
        levelUnlocked = Utils.updateHSV(ImageManager.loadImageFromResource(getActivity(), R.drawable.level_unlocked, LengthManager.getLevelFrameWidth(), LengthManager.getLevelFrameHeight()), cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);

        ViewPager viewPager = (ViewPager) layout.findViewById(R.id.levels_view_pager);
        viewPager.setAdapter(new LevelsViewPagerAdapter(this));

        ImageView levelsBackTop = (ImageView) layout.findViewById(R.id.levels_back_top);
        ImageView levelsBackBottom = (ImageView) layout.findViewById(R.id.levels_back_bottom);


        Utils.resizeView(viewPager, LengthManager.getScreenWidth(), LengthManager.getLevelsViewpagerHeight());
        Utils.resizeView(levelsBackTop, LengthManager.getScreenWidth(), LengthManager.getLevelsBackTopHeight());
        Utils.resizeView(levelsBackBottom, LengthManager.getScreenWidth(), LengthManager.getLevelsBackBottomHeight());

        setUpIndicatorLayout(layout, viewPager);

        return layout;
    }

    private void setUpIndicatorLayout(View view, final ViewPager viewPager) {
        final int count = viewPager.getAdapter().getCount();
        Log.d("indicator", "button_count " + count);

        final LinearLayout indicatorList = (LinearLayout) view.findViewById(R.id.indicator);
        Utils.resizeView(indicatorList, ViewGroup.LayoutParams.MATCH_PARENT, LengthManager.getIndicatorBigSize());

        for (int i = 0; i < count; i++) {
            ImageView indicator = new ImageView(view.getContext());
            final int finalI = i;
            indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(finalI);
                }
            });
            indicator.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            indicator.setImageResource(R.drawable.indicator);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LengthManager.getIndicatorSmallSize(), LengthManager.getIndicatorSmallSize());
            layoutParams.leftMargin = layoutParams.rightMargin = LengthManager.getIndicatorSmallSize() / 4;
            //indicator.setImageBitmap(ImageManager.loadImageFromResource(view.getContext(), R.drawable.digit_9, size, size));
            indicatorList.addView(indicator, layoutParams);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                updateIndicators(indicatorList, i + v);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    void updateIndicators(LinearLayout indicatorList, float position) {
        Log.d("indicator", "is called");
        float extra = LengthManager.getIndicatorBigSize() - LengthManager.getIndicatorSmallSize();
        float base = LengthManager.getIndicatorSmallSize();
        for (int i = 0; i < indicatorList.getChildCount(); i++) {
            float distance = 1 - Math.min(Math.abs(position - i), 1F);
            float size = distance * extra + base;
            Utils.resizeView(indicatorList.getChildAt(i), (int) size, (int) size);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadingManager.endTask();
    }

    public Package getPackage() {
        return mPackage;
    }

    public Bitmap getThumbnail(int levelID) {
        return null;
    }

    public Bitmap getLevelLockedBitmap() {
        return levelLocked;
    }


}

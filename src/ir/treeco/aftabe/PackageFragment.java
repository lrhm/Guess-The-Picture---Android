package ir.treeco.aftabe;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ir.treeco.aftabe.packages.Package;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.LoadingManager;
import ir.treeco.aftabe.utils.Utils;

import java.io.InputStream;

/**
 * Created by hossein on 8/31/14.
 */
public class PackageFragment extends Fragment {
    private Package mPackage;
    private Bitmap levelLocked;
    private Bitmap levelUnlocked;
    private Bitmap[] thubmnails;

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

        {
            int[] backgroundColor = mPackage.meta.getColor();

            {
                String tmp = "colors: ";
                for (int color : backgroundColor)
                    tmp += " " + Integer.toHexString(color);
                Log.e("COLORS", tmp);
            }

            View mainView = getActivity().findViewById(R.id.main_view);
            Utils.setViewBackground(mainView, new BackgroundDrawable(getActivity(), backgroundColor));
        }


        InputStream[] inputStreams = mPackage.getThumbnails();
        thubmnails = new Bitmap[inputStreams.length];

        for (int i = 0; i < inputStreams.length; i++) {
            thubmnails[i] = ImageManager.loadImageFromInputStream(inputStreams[i], LengthManager.getLevelFrameWidth() / 2, LengthManager.getLevelFrameHeight() / 2);
            inputStreams[i] = null;
        }

        {
            ViewPager viewPager = (ViewPager) layout.findViewById(R.id.levels_view_pager);
            Utils.resizeView(viewPager, LengthManager.getScreenWidth(), LengthManager.getLevelsViewpagerHeight());
            viewPager.setAdapter(new LevelsViewPagerAdapter(this));

            int currentPage = 0;
            while (16 * (currentPage + 1) < mPackage.getNumberOfLevels() && !mPackage.getLevel(16 * (currentPage + 1)).isLocked())
                currentPage++;

            viewPager.setCurrentItem(currentPage);
            setUpIndicatorLayout(layout, viewPager);
        }

        {
            ImageView levelsBackTop = (ImageView) layout.findViewById(R.id.levels_back_top);
            levelsBackTop.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), R.drawable.levels_back_top, LengthManager.getScreenWidth(), LengthManager.getLevelsBackTopHeight()));
            Utils.resizeView(levelsBackTop, LengthManager.getScreenWidth(), LengthManager.getLevelsBackTopHeight());
        }

        {
            ImageView levelsBackBottom = (ImageView) layout.findViewById(R.id.levels_back_bottom);
            levelsBackBottom.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), R.drawable.levels_back_bottom, LengthManager.getScreenWidth(), LengthManager.getLevelsBackBottomHeight()));
            Utils.resizeView(levelsBackBottom, LengthManager.getScreenWidth(), LengthManager.getLevelsBackBottomHeight());
        }


        return layout;
    }

    private void setUpIndicatorLayout(View view, final ViewPager viewPager) {
        final int count = viewPager.getAdapter().getCount();

        final LinearLayout indicatorList = (LinearLayout) view.findViewById(R.id.indicator);
        Utils.resizeView(indicatorList, ViewGroup.LayoutParams.MATCH_PARENT, LengthManager.getIndicatorBigSize());

        for (int i = 0; i < count; i++) {
            ImageView indicator = new ImageView(view.getContext());
            final int finalI = i;
            indicator.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                        viewPager.setCurrentItem(finalI);
                    return false;
                }
            });
            indicator.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            indicator.setImageResource(R.drawable.indicator);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LengthManager.getIndicatorSmallSize(), LengthManager.getIndicatorSmallSize());
            layoutParams.leftMargin = layoutParams.rightMargin = LengthManager.getIndicatorSmallSize() / 4;
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

        updateIndicators(indicatorList, viewPager.getCurrentItem());
    }

    void updateIndicators(LinearLayout indicatorList, float position) {
        float extra = LengthManager.getIndicatorBigSize() - LengthManager.getIndicatorSmallSize();
        float base = LengthManager.getIndicatorSmallSize();
        for (int i = 0; i < indicatorList.getChildCount(); i++) {
            float distance = 1 - Math.min(Math.abs(position - i), 1F);
            float size = distance * extra + base;
            Utils.resizeView(indicatorList.getChildAt(i), (int) size, (int) size);
            indicatorList.getChildAt(i).setLayoutParams(indicatorList.getChildAt(i).getLayoutParams());
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
        return thubmnails[levelID];
    }

    public Bitmap getLevelLockedBitmap() {
        return levelLocked;
    }


    public Bitmap getLevelUnockedBitmap() {
        return levelUnlocked;
    }
}

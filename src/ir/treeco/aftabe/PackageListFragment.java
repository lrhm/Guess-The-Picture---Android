package ir.treeco.aftabe;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.synchronization.Synchronizer;
import ir.treeco.aftabe.utils.*;

import static android.view.ViewGroup.*;

public class PackageListFragment extends Fragment implements AbsListView.OnScrollListener {

    private LinearLayout tabBar;
    private AutoCropListView packages;

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        int barTop = 0;
        if (i == 0) {
            RelativeLayout relativeLayout = (RelativeLayout) packages.getChildAt(0);
            if (relativeLayout != null) {
                View innerView = relativeLayout.getChildAt(0);
                innerView.setPadding(0, - relativeLayout.getTop(), 0, 0);
                barTop = Math.max(relativeLayout.getTop() + innerView.getHeight(), barTop);
            }
        }
        try {
            tabBar.setTranslationY(barTop);
        } catch (java.lang.NoSuchMethodError ignore) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(tabBar, "translationY", 0, barTop).setDuration(0);
            final int finalBarTop = barTop;
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    MarginLayoutParams lp = (MarginLayoutParams) tabBar.getLayoutParams();
                    lp.topMargin = finalBarTop;
                    tabBar.setLayoutParams(lp);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
//            TranslateAnimation anim = new TranslateAnimation(0, 0, barTop, barTop);
//            anim.setFillAfter(true);
//            anim.setDuration(0);
//            tabBar.startAnimation(anim);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_packages_list, container, false);
        packages =  (AutoCropListView) layout.findViewById(R.id.package_list);
        tabBar = (LinearLayout) layout.findViewById(R.id.tab_bar);

        FrameLayout.LayoutParams tabBarLayoutParams = (FrameLayout.LayoutParams) tabBar.getLayoutParams();
        tabBarLayoutParams.width = LengthManager.getScreenWidth();
        tabBarLayoutParams.height = LengthManager.getTabBarHeight();

        LinearLayout tabs = (LinearLayout) tabBar.getChildAt(0);
        Utils.resizeView(tabs, LengthManager.getScreenWidth(), LengthManager.getTabsHeight());

        ImageView shade = (ImageView) tabBar.getChildAt(1);
        shade.setImageBitmap(ImageManager.loadImageFromResource(inflater.getContext(), R.drawable.shadow_top, LengthManager.getScreenWidth(), LengthManager.getTabBarShadeHeight()));
        Utils.resizeView(shade, LengthManager.getScreenWidth(), LengthManager.getTabBarShadeHeight());

        PackageManager pManager = new PackageManager(getActivity(), layout);
        Synchronizer.setPackageManager(pManager);
        pManager.setActivity((IntroActivity) getActivity());
        try {
            pManager.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final PackageListAdapter adapter = new PackageListAdapter((IntroActivity) getActivity(), pManager);
        pManager.setAdapter(adapter);

        packages.setAdapter(adapter);
        adapter.setFilter(1);
        adapter.notifyDataSetChanged();

        packages.setOnScrollListener(this);

        final TextView[] textViews = new TextView[] {
                (TextView) layout.findViewById(R.id.hot_tab), // HOT tab
                (TextView) layout.findViewById(R.id.local_tab), // Local tab
                (TextView) layout.findViewById(R.id.new_tab)  // New tab
        };

        final int[] filterIDs = new int[] {
                PackageListImplicitAdapter.HOT_TAB,
                PackageListImplicitAdapter.LOCAL_TAB,
                PackageListImplicitAdapter.NEW_TAB
        };

        for (int i = 0; i < 3; ++i) {
            TextView textView = textViews[i];
            final int filterID = filterIDs[i];
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Do not refresh the same page
                    if (adapter.getFilter() == filterID)
                        return;

                    adapter.setFilter(filterID);

                    packages.setSelection(0);
                    for (int i = 0; i < 3; i++)
                        Utils.setViewBackground(textViews[i], filterIDs[i] == filterID ? new RoundedCornerDrawable() : null);
                }
            };
            textView.setOnClickListener(onClickListener);
            if (i == PackageListImplicitAdapter.LOCAL_TAB)
                onClickListener.onClick(textView);
            textView.setTypeface(FontsHolder.getTabBarFont(layout.getContext()));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getScreenWidth() / 17);
        }

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        View mainView = getActivity().findViewById(R.id.main_view);
        Utils.setViewBackground(mainView, new BackgroundDrawable(this.getActivity(), new int[]{
                Color.parseColor("#F3C81D"),
                Color.parseColor("#F3C01E"),
                Color.parseColor("#F49C14")
        }));

        LoadingManager.endTask();
    }

    public static PackageListFragment newInstance() {
        return new PackageListFragment();
    }
}

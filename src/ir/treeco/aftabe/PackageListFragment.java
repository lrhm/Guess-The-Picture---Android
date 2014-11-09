package ir.treeco.aftabe;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.utils.*;

public class PackageListFragment extends Fragment implements AbsListView.OnScrollListener{

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
            TranslateAnimation anim = new TranslateAnimation(0, 0, barTop, barTop);
            anim.setFillAfter(true);
            anim.setDuration(0);
            tabBar.startAnimation(anim);
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

        PackageManager pManager = new PackageManager(getActivity());
        try {
            pManager.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final PackageListAdapter aAdapter = new PackageListAdapter((IntroActivity) getActivity(), pManager);

//        final PackageListAdapter newAdapter = new PackageListAdapter((IntroActivity) getActivity(), pManager, PackageListImplicitAdapter.NEW_TAB_ADAPTER);
//        final PackageListAdapter localAdapter = new PackageListAdapter((IntroActivity) getActivity(), pManager, PackageListImplicitAdapter.LOCAL_TAB_ADAPTER);
//        final PackageListAdapter hotAdapter = new PackageListAdapter((IntroActivity) getActivity(), pManager, PackageListImplicitAdapter.HOT_TAB_ADAPTER);

        packages.setAdapter(aAdapter);
        aAdapter.setFilter(1);
        aAdapter.notifyDataSetChanged();

//        packages.setAdapter(localAdapter);
//        newAdapter.setFilter(0);
//        newAdapter.notifyDataSetChanged();


        packages.setOnScrollListener(this);

        final TextView[] textViews = new TextView[] {
                (TextView) layout.findViewById(R.id.tab_1), // HOT tab
                (TextView) layout.findViewById(R.id.tab_2), // Local tab
                (TextView) layout.findViewById(R.id.tab_3)  // New tab
        };

//        final PackageListAdapter[] adpaters = new PackageListAdapter[] {
//                hotAdapter,
//                localAdapter,
//                newAdapter
//        };

        for (int i = 0; i < 3; ++i) {
            TextView textView = textViews[i];
//            final PackageListAdapter adapter = adpaters[i];
            final int finalI = i;
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    packages.setAdapter(adapter);
//                    adapter.setFilter(0);
                    aAdapter.setFilter(finalI);
                    packages.setSelection(0);
                    for (int i = 0; i < 3; i++)
                        Utils.setViewBackground(textViews[i], i == finalI ? new RoundedCornerDrawable() : null);
                }
            };
            textView.setOnClickListener(onClickListener);
            if (i == PackageListImplicitAdapter.LOCAL_TAB_ADAPTER)
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

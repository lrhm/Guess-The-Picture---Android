package ir.treeco.aftabe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.utils.FontsHolder;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 8/12/14.
 */

class PackagesListScrollListener implements  AbsListView.OnScrollListener {

    private ListView packages;
    private View tabBar;

    PackagesListScrollListener(ListView packages, View tabBar) {
        this.packages = packages;
        this.tabBar = tabBar;
    }

    public void updateAdViewPadding() {

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        updateAdViewPadding();
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        updateAdViewPadding();
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
}

public class PackageListFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_packages_list, container, false);
        final AutoCropListView packages =  (AutoCropListView) layout.findViewById(R.id.package_list);
        final LinearLayout tabBar = (LinearLayout) layout.findViewById(R.id.tab_bar);

        FrameLayout.LayoutParams tabBarLayoutParams = (FrameLayout.LayoutParams) tabBar.getLayoutParams();
        tabBarLayoutParams.width = LengthManager.getScreenWidth();
        tabBarLayoutParams.height = LengthManager.getTabBarHeight();
        tabBar.setLayoutParams(tabBarLayoutParams);

        LinearLayout tabs = (LinearLayout) tabBar.getChildAt(0);
        tabs.setLayoutParams(new LinearLayout.LayoutParams(LengthManager.getScreenWidth(), LengthManager.getTabsHeight()));

        ImageView shade = (ImageView) tabBar.getChildAt(1);
        shade.setImageBitmap(ImageManager.loadImageFromResource(inflater.getContext(), R.drawable.shadow_top, LengthManager.getScreenWidth(), LengthManager.getTabBarShadeHeight()));
        shade.setLayoutParams(new LinearLayout.LayoutParams(LengthManager.getScreenWidth(), LengthManager.getTabBarShadeHeight()));

        PackageManager pManager = new PackageManager(getActivity());
        try {
            pManager.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final PackageListAdapter newAdapter = new PackageListAdapter(getActivity(), pManager, PackageListImplicitAdapter.NEW_TAB_ADAPTER);
        final PackageListAdapter localAdapter = new PackageListAdapter(getActivity(), pManager, PackageListImplicitAdapter.LOCAL_TAB_ADAPTER);
        final PackageListAdapter hotAdapter = new PackageListAdapter(getActivity(), pManager, PackageListImplicitAdapter.HOT_TAB_ADAPTER);

        packages.setAdapter(newAdapter);
        newAdapter.setFilter(0);
        newAdapter.notifyDataSetChanged();


        packages.setOnScrollListener(new PackagesListScrollListener(packages, tabBar));

        TextView[] textViews = new TextView[] {
                (TextView) layout.findViewById(R.id.tab_1), // HOT tab
                (TextView) layout.findViewById(R.id.tab_2), // Local tab
                (TextView) layout.findViewById(R.id.tab_3)  // New tab
        };

        final PackageListAdapter[] adpaters = new PackageListAdapter[] {
                hotAdapter,
                localAdapter,
                newAdapter
        };

        for (int i=0;i<3;++i) {
            TextView textView = textViews[i];
            final PackageListAdapter adapter = adpaters[i];
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    packages.setAdapter(adapter);
                    adapter.setFilter(0);
                    packages.setSelection(0);
                }
            });
            textView.setTypeface(FontsHolder.getTabBarFont(layout.getContext()));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getScreenWidth() / 17);
        }


        return layout;
    }
}

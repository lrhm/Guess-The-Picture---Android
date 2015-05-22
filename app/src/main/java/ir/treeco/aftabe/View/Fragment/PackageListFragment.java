package ir.treeco.aftabe.View.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ir.treeco.aftabe.Adapter.PackageListAdapter;
import ir.treeco.aftabe.Adapter.PackageListImplicitAdapter;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.RoundedCornerDrawable;
import ir.treeco.aftabe.View.Activity.IntroActivity;
import ir.treeco.aftabe.View.ListView.AutoCropListView;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.utils.FontsHolder;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.LoadingManager;
import ir.treeco.aftabe.utils.Utils;

public class PackageListFragment extends Fragment implements AbsListView.OnScrollListener {

    private static final String TAG = "PackageListFragment";
    private LinearLayout tabBar;
    private AutoCropListView packages;
    private PackageListAdapter adapter;
    private TextView[] textViews;
    private int[] filterIDs;

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
                innerView.setPadding(0, -relativeLayout.getTop(), 0, 0);
                barTop = Math.max(relativeLayout.getTop() + innerView.getHeight(), barTop);
            }
        }

        if (Build.VERSION.SDK_INT >= 11) {
            tabBar.setTranslationY(barTop);
        } else {
            tabBar.setPadding(0, barTop, 0, 0);
            tabBar.getLayoutParams().height = barTop + LengthManager.getTabBarHeight();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_packages_list, container, false);
        packages = (AutoCropListView) layout.findViewById(R.id.package_list);
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
//        Synchronizer.setPackageManager(pManager);
        pManager.setActivity((IntroActivity) getActivity());
        try {
            pManager.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new PackageListAdapter((IntroActivity) getActivity(), pManager);

        pManager.setAdapter(adapter);

        packages.setAdapter(adapter);
        packages.setOnScrollListener(this);

        textViews = new TextView[]{
                (TextView) layout.findViewById(R.id.hot_tab), // HOT tab
                (TextView) layout.findViewById(R.id.local_tab), // Local tab
                (TextView) layout.findViewById(R.id.new_tab)  // New tab
        };

        filterIDs = new int[]{
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
                    selectTab(filterID);
                }
            };
            textView.setOnClickListener(onClickListener);
            if (i == PackageListImplicitAdapter.LOCAL_TAB)
                onClickListener.onClick(textView);
            textView.setTypeface(FontsHolder.getTabBarFont(layout.getContext()));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getScreenWidth() / 17);
        }

        try {
            Method setEnableExcessScroll = packages.getClass().getMethod("setEnableExcessScroll", Boolean.TYPE);
            setEnableExcessScroll.invoke(packages, false);
        } catch (SecurityException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalArgumentException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        }

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectTab(PackageListImplicitAdapter.LOCAL_TAB);
    }

    void selectTab(int filterID) {
        // Do not refresh the same page
        if (adapter.getFilter() == filterID)
            return;

        adapter.setFilter(filterID);
        adapter.notifyDataSetChanged();

        if (packages.getFirstVisiblePosition() > 0)
            packages.setSelection(1);

        for (int i = 0; i < 3; i++)
            Utils.setViewBackground(textViews[i], filterIDs[i] == filterID ? new RoundedCornerDrawable() : null);
    }

    @Override
    public void onResume() {
        super.onResume();

        packages.setSelection(0);

//        View mainView = getActivity().findViewById(R.id.main_view);
//        Utils.setViewBackground(mainView, new BackgroundDrawable(this.getActivity(), new int[]{
//                Color.parseColor("#F3C81D"),
//                Color.parseColor("#F3C01E"),
//                Color.parseColor("#F49C14")
//        }));

        LoadingManager.endTask();
    }

    public static PackageListFragment newInstance() {
        return new PackageListFragment();
    }
}

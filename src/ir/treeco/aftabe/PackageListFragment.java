package ir.treeco.aftabe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

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
            LinearLayout linearLayout = (LinearLayout) packages.getChildAt(0);
            if (linearLayout != null) {
                View innerView = linearLayout.getChildAt(0);
                innerView.setPadding(0, - linearLayout.getTop(), 0, 0);
                barTop = Math.max(linearLayout.getTop() + innerView.getHeight(), barTop);
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
        final ListView packages =  (ListView) layout.findViewById(R.id.package_list);
        final View tabBar = layout.findViewById(R.id.tab_bar);

        final PackageListAdapter adapter = new PackageListAdapter(getActivity());
        packages.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        packages.setOnScrollListener(new PackagesListScrollListener(packages, tabBar));

        layout.findViewById(R.id.tab_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setFilter(0);
                packages.scrollTo(0, 0);
            }
        });
        layout.findViewById(R.id.tab_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setFilter(1);
                packages.scrollTo(0, 0);
            }
        });
        layout.findViewById(R.id.tab_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setFilter(0);
                packages.scrollTo(0, 0);
            }
        });

        return layout;
    }
}

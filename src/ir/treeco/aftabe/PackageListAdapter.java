package ir.treeco.aftabe;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.utils.FontsHolder;
import ir.treeco.aftabe.utils.LengthManager;

import java.util.Random;

/**
 * Created by hamed on 8/14/14.
 */
public class PackageListAdapter extends BaseAdapter {
    private final IntroActivity context;
    PackageListImplicitAdapter mAdapter;
    private int mCount;

    public PackageListAdapter(IntroActivity activity, PackageManager pManager) {
        this.context = activity;
        mAdapter = new PackageListImplicitAdapter(activity, pManager);
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                notifyDataSetInvalidated();
            }
        });
    }

    int getColumnCount() {
        return LengthManager.getPackagesListColumnCount();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    void refreshData() {
        mCount = (mAdapter.getCount() + getColumnCount() - 1) / getColumnCount() + 3 /* Tab bar and Ad view pager */;
    }


    @Override
    public void notifyDataSetChanged() {
        refreshData();
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        refreshData();
        super.notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    void setFilter(int filter) {
        mAdapter.setFilter(filter);
        notifyDataSetChanged();
    }

    public int getFilter() {
        return mAdapter.getFilter();
    }


    public View getAdView(View rowView) {
        if (rowView != null)
            return rowView;
        AdItemAdapter adItemAdapter = new AdItemAdapter(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout adHolder = (RelativeLayout) inflater.inflate(R.layout.view_ad_item, null);
        AutoScrollViewPager viewPager = (AutoScrollViewPager) adHolder.findViewById(R.id.ad_view_pager);
        viewPager.setAdapter(adItemAdapter);
        viewPager.setInterval(5000);
        viewPager.startAutoScroll(5000);
        int height = adItemAdapter.getCount() == 0? 0: LengthManager.getAdViewPagerHeight();
        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(LengthManager.getScreenWidth(), height));
        viewPager.setOffscreenPageLimit(1);
        if (adItemAdapter.getCount() > 0)
            viewPager.setCurrentItem(new Random().nextInt(adItemAdapter.getCount()));
        adHolder.setLayoutParams(new AbsListView.LayoutParams(LengthManager.getScreenWidth(), height));
        return adHolder;
    }

    @Override
    public View getView(int i, View rowView, ViewGroup viewGroup) {
        // TODO: Remove this
        if (i >= mCount)
            i = 1;

        if (i == 0)
            return getAdView(rowView);

        if (i == 1)
            return getSpaceView(rowView);

        if (i == mCount - 1)
            return getPlaceHolderView(rowView);

        i -= 2;

        if (rowView == null) {
            //Log.d("Aftabe+", "new row!");
            rowView = new LinearLayout(context);
            rowView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        LinearLayout linearLayout = (LinearLayout) rowView;
        for (int j = i * getColumnCount(), index = 0; index < getColumnCount(); j++, index++) {
            if (j < mAdapter.getCount()) {
                if (index < linearLayout.getChildCount()) {
                    View child = linearLayout.getChildAt(index);
                    child.setVisibility(View.VISIBLE);
                    mAdapter.getView(j, child, linearLayout);
                } else {
                    linearLayout.addView(mAdapter.getView(j, null, linearLayout));
                }
            } else {
                if (index < linearLayout.getChildCount())
                    linearLayout.getChildAt(index).setVisibility(View.INVISIBLE);
            }
        }
        return linearLayout;
    }

    private View getSpaceView(View rowView) {
        if (rowView != null)
            return rowView;
        rowView = new View(context);
        rowView.setLayoutParams(new AbsListView.LayoutParams(LengthManager.getScreenWidth(), LengthManager.getTabBarHeight()));
        return rowView;
    }

    private View getPlaceHolderView(View rowView) {
        TextView textView;
        if (rowView == null) {
            textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(FontsHolder.getYekan(context));
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getPlaceHolderTextSize());
        } else {
            textView = (TextView) rowView;
        }
        String text = "";
        if (mAdapter.getCount() == 0)
            text = context.getString(R.string.empty_list);
        textView.setText(text);
        textView.setLayoutParams(new AbsListView.LayoutParams(LengthManager.getScreenWidth(), getPlaceHolderHeight()));
        return textView;
    }

    @Override
    public int getItemViewType(int i) {
        // TODO: Remove this
        if (i >= mCount)
            i = 1;

        if (i == mCount - 1)
            return 3;
        return Math.min(i, 2);
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public int getTotalHeight() {
        return LengthManager.getTabBarHeight() + (mCount - 3) * LengthManager.getPackageIconSize();
    }

    public int getPlaceHolderHeight() {
        return Math.max(0, LengthManager.getScreenHeight() - LengthManager.getHeaderHeight() - getTotalHeight());
    }
}

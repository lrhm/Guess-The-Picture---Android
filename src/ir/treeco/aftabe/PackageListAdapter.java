package ir.treeco.aftabe;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 8/14/14.
 */
public class PackageListAdapter extends BaseAdapter {
    private final Context context;
    PackageListImplicitAdapter mAdapter;
    static final int COLUMN_COUNT = 2;
    private int mCount;

    public PackageListAdapter(Context context) {
        this.context = context;
        mAdapter = new PackageListImplicitAdapter(context);
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

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    void refreshData() {
        mCount = (mAdapter.getCount() + COLUMN_COUNT - 1) / COLUMN_COUNT;
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

    void setFilter(int shape) {
        mAdapter.setFilter(shape);
    }

    public View getAdView(View rowView) {
        if (rowView != null)
            return rowView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.ad_item, null);
        AutoScrollViewPager viewPager = (AutoScrollViewPager) linearLayout.findViewById(R.id.ad_view_pager);
        viewPager.setAdapter(new AdItemAdapter(context));
        viewPager.setInterval(5000);
        viewPager.startAutoScroll(5000);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LengthManager.getScreenWidth() / 2));
        viewPager.setOffscreenPageLimit(1);
        return linearLayout;
    }

    @Override
    public View getView(int i, View rowView, ViewGroup viewGroup) {
        if (i == 0)
            return getAdView(rowView);
        i--;

        if (rowView == null) {
            //Log.d("Aftabe+", "new row!");
            rowView = new LinearLayout(context);
            rowView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        LinearLayout linearLayout = (LinearLayout) rowView;
        for (int j = i * COLUMN_COUNT, index = 0; j < (i + 1) * COLUMN_COUNT; j++, index++) {
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

    @Override
    public int getItemViewType(int i) {
        return i == 0? 0: 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

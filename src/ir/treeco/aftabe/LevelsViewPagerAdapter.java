package ir.treeco.aftabe;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import ir.treeco.aftabe.packages.Package;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.LoadingManager;
import ir.treeco.aftabe.utils.TaskStartedListener;

import java.util.Arrays;

/**
 * Created by hamed on 9/2/14.
 */
public class LevelsViewPagerAdapter extends PagerAdapter {
    private final Package mPackage;
    private final PackageFragment fragment;
    private LayoutInflater inflater;

    public LevelsViewPagerAdapter(PackageFragment fragment) {
        this.mPackage = fragment.getPackage();
        this.fragment = fragment;
        this.inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return (mPackage.getNumberOfLevels() + LengthManager.getPageLevelCount() - 1) / LengthManager.getPageLevelCount();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final int levelIDs[] = new int[LengthManager.getPageLevelCount()];
        Arrays.fill(levelIDs, -1);
        final int begin = position * LengthManager.getPageLevelCount();
        final int end = Math.min(mPackage.getNumberOfLevels(), (position + 1) * LengthManager.getPageLevelCount());
        for (int current = begin; current < end; current++)
            levelIDs[current - begin] = current;

        GridView gridView = (GridView) inflater.inflate(R.layout.levels_list_view, container, false);
        gridView.setNumColumns(LengthManager.getPageColumnCount());
        gridView.setColumnWidth(LengthManager.getLevelFrameWidth());
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding(LengthManager.getLevelsGridViewLeftRightPadding(), LengthManager.getLevelsGridViewTopAndBottomPadding(), LengthManager.getLevelsGridViewLeftRightPadding(), LengthManager.getLevelsGridViewTopAndBottomPadding());

        gridView.setAdapter(new LevelsGridViewAdapter(levelIDs, fragment));

        gridView.setOnTouchListener(new GridView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (levelIDs[i] == -1)
                    return;
                LoadingManager.startTask(new TaskStartedListener() {
                    @Override
                    public void taskStarted() {
                        LevelFragment newFragment = LevelFragment.newInstance(mPackage.getLevel(levelIDs[i]));
                        FragmentTransaction transaction = fragment.getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            }
        });

        container.addView(gridView);
        return gridView;
    }

    @Override
    synchronized public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((GridView) object);
    }
}


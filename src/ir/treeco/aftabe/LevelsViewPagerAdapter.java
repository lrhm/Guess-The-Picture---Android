package ir.treeco.aftabe;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
    private final FragmentActivity fragmentActivity;
    private LayoutInflater inflater;
    //private GridView[] gridViews;
    private int timesClicked;

    public LevelsViewPagerAdapter(Package mPackage, FragmentActivity fragmentActivity) {
        this.mPackage = mPackage;
        this.fragmentActivity = fragmentActivity;
        this.inflater = (LayoutInflater) fragmentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //this.gridViews = new GridView[getCount()];
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
        /*if (gridViews[position] != null) {
            container.addView(gridViews[position]);
            return gridViews[position];
        }*/

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

        gridView.setAdapter(new LevelsGridViewAdapter(levelIDs, mPackage));

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
                /*if (LevelDataOrganizer.getLevel(levelIDs[i]).isLocked(preferences)) {
                    timesClicked++;
                    if (timesClicked == 4) Toast.makeText(BoxActivity.this, "قفله برادر!", Toast.LENGTH_LONG).show();
                    if (timesClicked == 7) Toast.makeText(BoxActivity.this, "گفتم که قفله :|", Toast.LENGTH_LONG).show();
                    if (timesClicked == 15) Toast.makeText(BoxActivity.this, "به قرآن قفله!", Toast.LENGTH_LONG).show();
                    if (timesClicked == 20) Toast.makeText(BoxActivity.this, "به روح اعتقاد داری؟", Toast.LENGTH_LONG).show();
                    if (timesClicked == 30) Toast.makeText(BoxActivity.this, "تو روحت! :))", Toast.LENGTH_LONG).show();
                    if (timesClicked == 75) Toast.makeText(BoxActivity.this, "داری به جاهای خوبی می‌رسی!", Toast.LENGTH_LONG).show();
                    if (timesClicked == 100) Toast.makeText(BoxActivity.this, "۹۹۹۹ بار دیگه بزنی بهت ۹۹۹ تا سکه می‌دم!", Toast.LENGTH_LONG).show();
                    if (timesClicked == 100 + 9999) {
                        CoinManager.earnCoins(999, preferences);
                        Toast.makeText(BoxActivity.this, "سرطان شست گرفتی که! کافی بود ۸۰۰ تومن خرج می‌کردی! :))", 3 * Toast.LENGTH_LONG).show();
                        CoinManager.updateCoinView((TextView) findViewById(R.id.coinsCount), preferences);
                    }
                    // if (timesClicked == ?) Toast.makeText(BoxActivity.this, "", Toast.LENGTH_LONG).show();
                    return;
                }*/
                LoadingManager.startTask(new TaskStartedListener() {
                    @Override
                    public void taskStarted() {
                        LevelFragment fragment = LevelFragment.newInstance(mPackage.getLevel(levelIDs[i]));
                        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            }
        });

        //gridViews[position] = gridView;
        container.addView(gridView);
        return gridView;
    }

    @Override
    synchronized public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((GridView) object);
    }
}


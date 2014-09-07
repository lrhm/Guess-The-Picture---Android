package ir.treeco.aftabe;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import ir.treeco.aftabe.packages.Package;
import ir.treeco.aftabe.utils.LengthManager;

import java.util.Arrays;

/**
 * Created by hamed on 9/2/14.
 */
public class LevelsViewPagerAdapter extends PagerAdapter {
    private final Package mPackage;
    private final FragmentActivity fragmentActivity;
    private int timesClicked;
    private LayoutInflater inflater;

    public LevelsViewPagerAdapter(Package mPackage, FragmentActivity fragmentActivity) {
        this.mPackage = mPackage;
        this.fragmentActivity = fragmentActivity;
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

        // TODO move this into constructor
        inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.levels_list_view, container, false);

        GridView gridView = (GridView) viewLayout.findViewById(R.id.grid_view);
        gridView.setOnTouchListener(new GridView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        gridView.setNumColumns(LengthManager.getPageColumnCount());
        gridView.setColumnWidth(LengthManager.getLevelThumbnailSize());
        gridView.setStretchMode(GridView.NO_STRETCH);
        //gridView.setPadding(0, (int) heightManager.getGridViewTopAndBottomPadding(), 0, (int) heightManager.getGridViewTopAndBottomPadding());
        //gridView.setVerticalSpacing((int) heightManager.getGridViewVerticalSpacing());
        //gridView.setHorizontalSpacing((int) heightManager.getGridViewHorizontalSpacing());

        //final float gridViewWidth = heightManager.getGridViewHorizontalSpacing() * (HeightManager.BOX_NUM_OF_COLUMNS - 1) + heightManager.getLevelThumbnailSize() * HeightManager.BOX_NUM_OF_COLUMNS;
        //final float gridViewHeight = heightManager.getGridViewVerticalSpacing() * (BOX_NUM_OF_ROWS - 1) + heightManager.getLevelThumbnailSize() * BOX_NUM_OF_ROWS + 10;
        //Log.e("GOLVAZHE", "gvHeight " + gridViewHeight + 2 * heightManager.getGridViewTopAndBottomPadding());

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) gridView.getLayoutParams();
        //layoutParams.width = (int) gridViewWidth;
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        gridView.setLayoutParams(layoutParams);

        LevelsGridViewAdapter cellAdapter = new LevelsGridViewAdapter(levelIDs, mPackage);
        gridView.setAdapter(cellAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
                LevelFragment fragment = LevelFragment.newInstance(mPackage.getLevel(levelIDs[i]));
                FragmentTransaction transaction = ((FragmentActivity) fragmentActivity).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    synchronized public void destroyItem(ViewGroup container, int position, Object object) {
        FrameLayout frameLayout = (FrameLayout) object;
        //Log.e("GOLVAZHE", "Destroy called on " + position);
        GridView gridView = (GridView) frameLayout.findViewById(R.id.grid_view);
        //((LevelsGridViewAdapter) gridView.getAdapter()).killThreads();
        ((ViewPager) container).removeView(frameLayout);
    }
}


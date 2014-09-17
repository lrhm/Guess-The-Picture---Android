package ir.treeco.aftabe.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Pair;
import android.view.Display;
import android.view.WindowManager;
import ir.treeco.aftabe.R;

/**
 * Created by hamed on 8/12/14.
 */
public class LengthManager {
    private static boolean initialized = false;
    private static int screenHeight;
    private static int screenWidth;
    private static Context context;

    public static void initialize(Context context) {
        if (initialized)
            return;
        LengthManager.context = context;
        screenHeight = getScreenHeight(context);
        screenWidth = getScreenWidth(context);
        initialized = true;
    }

    @SuppressWarnings("all")
    private static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }

        return point.x;
    }

    @SuppressWarnings("all")
    private static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }

        return point.y;
    }

    public static int getScreenWidth() {
        if (!initialized)
            throw new IllegalStateException();
        return screenWidth;
    }

    public static int getScreenHeight() {
        if (!initialized)
            throw new IllegalStateException();
        return screenHeight;
    }

    public static Pair<Integer, Integer> getResourceDimensions(int resourceId) {
        Resources resources = context.getResources();
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resourceId, bounds);
        return new Pair<Integer, Integer>(bounds.outWidth, bounds.outHeight);
    }

    public static int getHeightWithFixedWidth(int resourceId, int width) {
        final Pair<Integer, Integer> dimensions = getResourceDimensions(resourceId);
        return dimensions.second * width / dimensions.first;
    }

    public static int getWidthWithFixedHeight(int resourceId, int height) {
        final Pair<Integer, Integer> dimensions = getResourceDimensions(resourceId);
        return dimensions.first * height / dimensions.second;
    }

    public static int getHeaderHeight() {
        return getScreenWidth() / 4;
    }

    public static int getTabsHeight() {
        return LengthManager.getHeightWithFixedWidth(R.drawable.tabbar_background, LengthManager.getScreenWidth());
    }

    public static int getTabBarShadeHeight() {
        return LengthManager.getHeightWithFixedWidth(R.drawable.shadow_top, LengthManager.getScreenWidth());
    }

    public static int getTabBarHeight() {
        return getTabsHeight() + getTabBarShadeHeight();
    }

    public static int getPageLevelCount() {
        return getPageRowCount() * getPageColumnCount();
    }

    public static int getPageColumnCount() {
        return 4;
    }

    public static int getPageRowCount() {
        return 4;
    }

    public static int getAlphabetButtonSize() {
        return getScreenWidth() / 8;
    }

    public static float getAlphabetFontSize() {
        return getScreenWidth() / 14;
    }

    public static int getSolutionButtonSize() {
        return getScreenWidth() / 12;
    }

    public static float getSolutionFontSize() {
        return getScreenWidth() / 24;
    }

    public static int getFragmentHeight() {
        return getScreenHeight() - getHeaderHeight();
    }

    public static int getLevelsBackTopHeight() {
        return LengthManager.getHeightWithFixedWidth(R.drawable.levels_back_top, LengthManager.getScreenWidth());
    }

    public static int getLevelsBackBottomHeight() {
        return LengthManager.getHeightWithFixedWidth(R.drawable.levels_back_bottom, LengthManager.getScreenWidth());
    }

    public static int getLevelsViewpagerHeight() {
        return getPageRowCount() * getLevelFrameHeight() + 2 * getLevelsGridViewTopAndBottomPadding();
    }

    public static int getLevelsGridViewLeftRightPadding() { return getScreenWidth() / 20; }

    public static int getLevelFrameWidth() {
        return (getScreenWidth() - 2 * getLevelsGridViewLeftRightPadding()) / 4;
    }

    public static int getLevelFrameHeight() {
        return getHeightWithFixedWidth(R.drawable.level_unlocked, getLevelFrameWidth());
    }

    public static int getLevelImageFrameWidth() {
        return getScreenWidth() * 93 / 100;
    }

    public static int getLevelImageFrameHeight() {
        return getHeightWithFixedWidth(R.drawable.frame, getLevelImageFrameWidth());
    }


    public static int getLevelThumbnailPadding() {
        return getLevelFrameWidth() / 7;
    }

    public static int getLevelsGridViewTopAndBottomPadding() {
        return 0;
    }

    public static int getLevelImageWidth() {
        return getLevelImageFrameWidth() * 927 / 997;
    }

    public static int getLevelImageHeight() {
        return getLevelImageFrameHeight() * 642 / 704;
    }

    public static int getIndicatorBigSize() { return getScreenWidth() / 15; }
    public static int getIndicatorSmallSize() { return getScreenWidth() / 30; }
}

package ir.treeco.aftabe2.Util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Pair;
import android.view.Display;
import android.view.WindowManager;

import ir.treeco.aftabe2.R;

public class LengthManager {
//    private static boolean initialized = false;
    private int screenHeight;
    private int screenWidth;
    private Context context;
    private Tools tools;

    public LengthManager(Context context) {
        this.context = context;
        screenHeight = getScreenHeight(context);
        screenWidth = getScreenWidth(context);
        tools = new Tools(context);
    }

    private int getScreenWidth(Context context) {
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

    private int getScreenHeight(Context context) {
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

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public Pair<Integer, Integer> getResourceDimensions(int resourceId) {
        Resources resources = context.getResources();
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resourceId, bounds);
        return new Pair<>(bounds.outWidth, bounds.outHeight);
    }

    public int getHeightWithFixedWidth(int resourceId, int width) {
        final Pair<Integer, Integer> dimensions = getResourceDimensions(resourceId);
        return dimensions.second * width / dimensions.first;
    }

    public int getWidthWithFixedHeight(int resourceId, int height) {
        final Pair<Integer, Integer> dimensions = getResourceDimensions(resourceId);
        return dimensions.first * height / dimensions.second;
    }

    public int getHeaderHeight() {
        return getScreenWidth() / 4;
    }

    public int getTabsHeight() {
        // TODO: Useless resource!
        return getHeightWithFixedWidth(R.drawable.tabbar_background, getScreenWidth());
    }

    public int getTabBarShadeHeight() {
        return getHeightWithFixedWidth(R.drawable.shadow_top, getScreenWidth());
    }

    public int getTabBarHeight() {
        return getTabsHeight() + getTabBarShadeHeight();
    }

    public int getPageLevelCount() {
        return getPageRowCount() * getPageColumnCount();
    }

    public int getPageColumnCount() {
        return 4;
    }

    public int getPageRowCount() {
        return 4;
    }

    public int getAlphabetButtonSize() {
        return getScreenWidth() / 8;
    }

    public float getAlphabetFontSize() {
        return getScreenWidth() / 14;
    }

    public int getSolutionButtonSize() {
        return getScreenWidth() / 12;
    }

    public float getSolutionFontSize() {
        return getScreenWidth() / 24;
    }

    public int getFragmentHeight() {
        return getScreenHeight() - getHeaderHeight();
    }

    public int getLevelsBackTopHeight() {
        return getHeightWithFixedWidth(R.drawable.levels_back_top, getScreenWidth());
    }

    public int getLevelsBackBottomHeight() {
        return getHeightWithFixedWidth(R.drawable.levels_back_bottom, getScreenWidth());
    }

    public int getLevelsViewpagerHeight() {
        return getPageRowCount() * getLevelFrameHeight() + 2 * getLevelsGridViewTopAndBottomPadding();
    }

    public int getLevelsGridViewLeftRightPadding() { return getScreenWidth() / 20; }

    public int getLevelFrameWidth() {
        return (getScreenWidth() - 2 * getLevelsGridViewLeftRightPadding()) / 4;
    }

    public int getLevelFrameHeight() {
        return (int) (getScreenWidth() * 0.235);
        //getHeightWithFixedWidth(R.drawable.level_unlocked, getLevelFrameWidth());
    }

    public int getLevelImageFrameWidth() {
        return getScreenWidth() * 93 / 100;
    }

    public int getLevelImageFrameHeight() {
        return getHeightWithFixedWidth(R.drawable.frame, getLevelImageFrameWidth());
    }


    public int getLevelThumbnailPadding() {
        return getLevelFrameWidth() * 10 / 100;
    }

    public int getLevelsGridViewTopAndBottomPadding() {
        return 0;
    }

    public int getLevelImageWidth() {
        return getLevelImageFrameWidth() * 927 / 997;
    }

    public int getLevelImageHeight() {
        return getLevelImageFrameHeight() * 642 / 704;
    }

    public int getIndicatorBigSize() { return getScreenWidth() / 15; }
    public int getIndicatorSmallSize() { return getScreenWidth() / 30; }

    public int getStoreDialogMargin() {
        return getScreenWidth() / 20;
    }

    public int getStoreDialogPadding() {
        return getScreenWidth() / 15;
    }

    public int getStoreItemsInnerWidth() {
        return getScreenWidth() - 2 * (getStoreDialogMargin() + getStoreDialogPadding());
    }

    public float getStoreItemFontSize() {
        return getScreenWidth() * 60 / 1000;
    }

    public int getStoreItemHeight() {
        return getHeightWithFixedWidth(R.drawable.single_button_red, getStoreItemWidth());
    }

    public int getStoreItemWidth() {
        return getScreenWidth() * 70 / 100;
    }

    public int getCheatButtonHeight() { return getHeightWithFixedWidth(R.drawable.cheat_right, getCheatButtonWidth()); }

    public int getCheatButtonWidth() {
        return getLevelImageWidth();
    }

    public float getCheatButtonFontSize() {
        return getScreenWidth() / 15;
    }

    public int getShopTitleWidth() {
        return getScreenWidth() / 2;
    }

    public int getToastWidth() {
        return getScreenWidth() * 90 / 100;
    }

    public float getToastFontSize() {
        return getScreenWidth() / 15f;
    }

    public int getCheatButtonSize() {
        return getScreenWidth() / 7;
    }

    public int getVideoPlayButtonSize() {
        return getLevelImageWidth() / 2;
    }

    public int getShopTitleBottomMargin() {
        return getShopTitleWidth() / 8;
    }

    public int getTickViewSize() {
        return getScreenWidth() / 3;
    }

    public int getLevelFinishedButtonsSize() {
        return getScreenWidth() / 6;
    }

    public float getLevelSolutionTextSize() {
        return getScreenWidth() / 15f;
    }


    public float getLevelAuthorTextSize() {
        return getScreenWidth() / 18f;
    }

    public int getLevelFinishedDialogPadding() {
        return getScreenWidth() / 10;
    }

    public int getOneFingerDragWidth() {
        return getLevelImageFrameWidth() / 4;
    }

    public int getResourceLockWidth() {
        return getLevelImageFrameWidth() / 3;
    }

    public int getToastPadding() {
        return getToastWidth() / 17;
    }

    public float getPackagePurchaseTitleSize() {
        return getScreenWidth() / 13f;
    }

    public float getPackagePurchaseDescriptionSize() {
        return getScreenWidth() / 19f;
    }

    public float getPackagePurchaseItemTextSize() {
        return getScreenWidth() / 22f;
    }

    public int getPackagePurchaseItemWidth() {
        return getScreenWidth() * 60 / 100;
    }
    public int getPackagePurchaseItemHeight() {
        return getPackagePurchaseItemWidth() * 504 / 1809;
    }

    public int getLoadingEhemWidth() {
        return getScreenWidth() / 3;
    }

    public int getLoadingToiletWidth() {
        return getScreenWidth() / 11;
    }

    public int getLoadingEhemPadding() {
        return getScreenWidth() / 11;
    }

    public int getPackagesListColumnCount() {
        return Math.min(Math.max((int) (tools.convertPixelsToDp(getScreenWidth()) / 150), 1), 3);
    }

    public int getLevelFinishedDialogTopPadding() {
        return getScreenWidth() / 30;
    }

    public int getPrizeBoxSize() {
        return getScreenWidth() / 5;
    }

    public float getTipTextSize() {
        return getScreenWidth() / 12.F;
    }
    public int getTipWidth() { return getScreenWidth() * 8 / 10; }

    public float getPlayStopButtonFontSize() {
        return getScreenWidth() / 3;
    }

    public int getAdViewPagerHeight() {
        return getScreenWidth() * 579 / 1248;
    }

    public int getPackageIconSize() {
        return getScreenWidth() / getPackagesListColumnCount();
    }

    public float getPlaceHolderTextSize() {
        return getScreenWidth() / 14.F;
    }
}

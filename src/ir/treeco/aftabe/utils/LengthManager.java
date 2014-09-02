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
        return getHeightWithFixedWidth(R.drawable.header, getScreenWidth());
    }

    public static int getTabBarHeight() {
        return LengthManager.getHeightWithFixedWidth(R.drawable.tabbar_background, LengthManager.getScreenWidth());
    }

    public static int getPageLevelCount() {
        return 16;
    }

    public static int getPageColumnCount() {
        return 4;
    }

    public static int getLevelThumbnailSize() {
        return getScreenWidth() / getPageColumnCount();
    }
}

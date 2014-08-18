package ir.treeco.aftabe.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by hamed on 8/12/14.
 */
public class LengthManager {
    private static boolean initialized = false;
    private static int screenHeight;
    private static int screenWidth;

    public static void initialize(Context context) {
        if (initialized)
            return;
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
}

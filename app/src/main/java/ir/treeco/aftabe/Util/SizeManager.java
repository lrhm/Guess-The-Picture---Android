package ir.treeco.aftabe.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;

public class SizeManager {


    static int screenHeight = 0;

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static void setScreenHeight(int screenHeight) {
        SizeManager.screenHeight = screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static void setScreenWidth(int screenWidth) {
        SizeManager.screenWidth = screenWidth;
    }

    static int screenWidth = 0;


    public static void initSizes(Activity context) {
        int screenWidth = 0;
        int screenHeight = 0;
        if (Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();

            // this.getWindowManager().getDefaultDisplay().getRealSize(size);

            context.getWindowManager().getDefaultDisplay().getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;


        } else {
            Display display = context.getWindowManager().getDefaultDisplay();
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }
        SizeManager.setScreenHeight(screenHeight);
        SizeManager.setScreenWidth(screenWidth);

    }


}

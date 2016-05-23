package ir.treeco.aftabe2.Util;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
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

    private static boolean initilized = false;


    public static void initSizes(Activity context) {

        if (initilized)
            return;
        initilized = true;

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

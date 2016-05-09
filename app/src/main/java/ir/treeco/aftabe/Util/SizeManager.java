package ir.treeco.aftabe.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;

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
        if (Build.VERSION.SDK_INT >= 11) {
            Point size = new Point();

            // this.getWindowManager().getDefaultDisplay().getRealSize(size);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                context.getWindowManager().getDefaultDisplay().getSize(size);
                screenWidth = size.x;
                screenHeight = size.y;
            }


            DisplayMetrics metrics = new DisplayMetrics();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context.getWindowManager().getDefaultDisplay()
                        .getRealMetrics(metrics);
                screenWidth = metrics.widthPixels;
                screenHeight = metrics.heightPixels;
            }


        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
        }
        SizeManager.setScreenHeight(screenHeight);
        SizeManager.setScreenWidth(screenWidth);

    }


}

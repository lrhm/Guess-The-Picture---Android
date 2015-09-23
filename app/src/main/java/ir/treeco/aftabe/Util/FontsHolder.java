package ir.treeco.aftabe.Util;

import android.content.Context;
import android.graphics.Typeface;

public class FontsHolder {
    private static Typeface nasrFont = null;
    private static Typeface homaFont = null;
    private static Typeface yekanFont = null;

    public static Typeface getTabBarFont(Context context) {
        if (nasrFont == null) nasrFont = Typeface.createFromAsset(context.getAssets(), "nasr.ttf");
        return nasrFont;
    }

    public static Typeface getHoma(Context context) {
        if (homaFont == null) homaFont = Typeface.createFromAsset(context.getAssets(), "homa.ttf");
        return homaFont;
    }

    public static Typeface getYekan(Context context) {
        if (yekanFont == null) yekanFont = Typeface.createFromAsset(context.getAssets(), "yekan.ttf");
        return yekanFont;
    }
}

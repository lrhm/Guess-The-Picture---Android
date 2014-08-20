package ir.treeco.aftabe.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created with IntelliJ IDEA.
 * User: hamed
 * Date: 3/1/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class FontsHolder {
    private static Typeface nasrFont = null;

    public static Typeface getTabBarFont(Context context) {
        if (nasrFont == null) nasrFont = Typeface.createFromAsset(context.getAssets(), "nasr.ttf");
        return nasrFont;
    }
}

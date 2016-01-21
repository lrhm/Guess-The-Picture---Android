package ir.treeco.aftabe.Util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

public class FontsHolder {
    private static Typeface nasrFont = null;
    private static Typeface homaFont = null;
    private static Typeface yekanFont = null;



    private static Typeface sansBold = null;
    private static Typeface sansMedium = null;
    private static Typeface sansRegular = null;
    private static Typeface sansLight = null;


    public static final int SANS_BOLD = 1;
    public static final int SANS_MEDIUM = 2;
    public static final int SANS_REGULAR = 4;
    public static final int SANS_LIGHT= 8;
    public static final int HOMA =16;

    public static Typeface getFont(Context context , int type){
        switch (type){
            case SANS_BOLD:
                return getSansBold(context);
            case SANS_LIGHT:
                return getSansLight(context);
            case SANS_MEDIUM:
                return getSansMedium(context);
            case SANS_REGULAR:
                return getSansRegular(context);
            case HOMA:
                return getHoma(context);
        }

        return null;

    }

    public static Typeface getSansBold(Context context) {
        if (sansBold == null ) sansBold = Typeface.createFromAsset(context.getAssets(), "sans_bold.ttf");
        return sansBold;
    }

    public static Typeface getSansMedium(Context context) {
        if (sansMedium == null ) sansMedium = Typeface.createFromAsset(context.getAssets(), "sans_medium.ttf");
        return sansMedium;
    }

    public static Typeface getSansLight(Context context) {
        if (sansLight == null ) sansLight = Typeface.createFromAsset(context.getAssets(), "sans_light.ttf");
        return sansLight;
    }

    public static Typeface getSansRegular(Context context) {

        if (sansRegular == null ) sansRegular = Typeface.createFromAsset(context.getAssets(), "sans_regular.ttf");
        return sansRegular;
    }

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


    public static void setFont(TextView textView , int type , int sizeSP){
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP , sizeSP);
        setFont(textView , type);
    }


    public static void setFont(TextView textView , int type ){
        textView.setTypeface(getFont(textView.getContext() , type));
    }
}

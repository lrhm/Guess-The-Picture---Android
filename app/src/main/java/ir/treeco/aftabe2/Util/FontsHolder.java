package ir.treeco.aftabe2.Util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

public class FontsHolder {

    private static Typeface sansBold = null;
    private static Typeface sansMedium = null;
    private static Typeface lond = null;
    private static Typeface sansBoldNumber = null;
    private static Typeface sansMediumNumber = null;


    public static final int SANS_BOLD = 1;
    public static final int SANS_MEDIUM = 2;
    public static final int SANS_BOLD_NUM = 4;
    public static final int SANS_MEDIUM_NUM = 8;
    public static final int LOND = 32;

    public static Typeface getFont(Context context , int type){
        switch (type){
            case SANS_BOLD:
                return getSansBold(context);
            case SANS_MEDIUM:
                return getSansMedium(context);
            case LOND:
                return getLond(context);
            case SANS_BOLD_NUM:
                return getNumeralSansBold(context);
            case SANS_MEDIUM_NUM:
                return getNumeralSansMedium(context);
        }

        return null;

    }

    public static Typeface getLond(Context context){
        if(lond == null) lond = Typeface.createFromAsset(context.getAssets(), "LondrinaSolid-Regular.ttf");
        return lond;

    }

    public static Typeface getSansBold(Context context) {
        if (sansBold == null ) sansBold = Typeface.createFromAsset(context.getAssets(), "sans_bold.ttf");
        return sansBold;
    }

    public static Typeface getSansMedium(Context context) {
        if (sansMedium == null ) sansMedium = Typeface.createFromAsset(context.getAssets(), "sans_medium.ttf");
        return sansMedium;
    }

    public static Typeface getNumeralSansBold(Context context) {
        if (sansBoldNumber == null ) sansBoldNumber = Typeface.createFromAsset(context.getAssets(), "sans_bold_num.ttf");
        return sansBoldNumber;
    }

    public static Typeface getNumeralSansMedium(Context context) {
        if (sansMediumNumber == null ) sansMediumNumber = Typeface.createFromAsset(context.getAssets(), "sans_medium_num.ttf");
        return sansMediumNumber;
    }


    public static void setFont(TextView textView , int type , int sizeSP){
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP , sizeSP);
        setFont(textView , type);
    }


    public static void setFont(TextView textView , int type ){
        textView.setTypeface(getFont(textView.getContext() , type));
    }
}

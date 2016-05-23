package ir.treeco.aftabe2.Util;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by root on 5/8/16.
 */
public class UiUtil {


    public static void setTextViewSize(TextView textView, int height, float scale) {

        float pixelTextSize = height * scale;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelTextSize);

    }

    public static int getTextViewWidth(TextView textView) {


        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(SizeManager.getScreenWidth(), View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredWidth();
    }

    public static int getTextViewHeight(TextView textView) {


        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(SizeManager.getScreenWidth(), View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    public static void setHeight(View view, int height) {
        view.getLayoutParams().height = height;
    }

    public static void setWidth(View view, int width) {
        view.getLayoutParams().width = width;
    }

    public static void setTopMargin(View view, int topMargin) {

        ViewParent parent = view.getParent();

        if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.topMargin = topMargin;
            return;
        }
        if (parent instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.topMargin = topMargin;
            return;
        }

        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.topMargin = topMargin;
            return;
        }

    }


    public static void setBottomMargin(View view, int bottomMargin) {

        ViewParent parent = view.getParent();

        if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.bottomMargin = bottomMargin;
            return;
        }
        if (parent instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.bottomMargin = bottomMargin;
            return;
        }

        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.bottomMargin = bottomMargin;
            return;
        }
    }

    public static void setLeftMargin(View view, int leftMargin) {

        ViewParent parent = view.getParent();

        if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = leftMargin;
            return;
        }
        if (parent instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = leftMargin;
            return;
        }
        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = leftMargin;
            return;
        }
    }


    public static void setRightMargin(View view, int rightMargin) {

        ViewParent parent = view.getParent();

        if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = rightMargin;
            return;
        }
        if (parent instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = rightMargin;
            return;
        }

        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = rightMargin;
            return;
        }
    }


    public static float getAdjustTextSize(TextView textView, int width, int height, int textSize, String request) {


        float res = Prefs.getFloat(request + "_TextSize", -10);
        if (res != -10)
            return res;


        String temp = "gsjskssgwaminsaf";
        temp = temp.substring(0, textSize);
        textView.setText(temp);

        float startPx = textView.getTextSize();
        while (getTextViewHeight(textView) < height && getTextViewWidth(textView) < width) {

            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, startPx);
            startPx += 1;
        }

        Prefs.putFloat(request + "_TextSize", startPx);

        return startPx;

    }


}

package ir.treeco.aftabe.Util;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    }


}

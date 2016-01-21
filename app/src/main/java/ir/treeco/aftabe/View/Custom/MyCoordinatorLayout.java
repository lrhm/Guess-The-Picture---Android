package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by al on 12/24/15.
 */
public class MyCoordinatorLayout extends CoordinatorLayout {

    private boolean allowForScrool = true;

    public MyCoordinatorLayout(Context context) {
        super(context);
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {


        return allowForScrool && super.onStartNestedScroll(child, target, nestedScrollAxes);
    }



    public boolean isAllowForScrool() {
        return allowForScrool;
    }

    public void setAllowForScrool(boolean allowForScrool) {
        this.allowForScrool = allowForScrool;
    }
}
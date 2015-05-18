package ir.treeco.aftabe.View.ListView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 9/5/14.
 */
public class AutoCropListView extends ListView {
    public AutoCropListView(Context context) {
        super(context);
    }

    public AutoCropListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoCropListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int saveCount = canvas.getSaveCount();
        if (getFirstVisiblePosition() > 0)
            canvas.clipRect(0, LengthManager.getTabsHeight(), canvas.getWidth(), canvas.getHeight());
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }
}

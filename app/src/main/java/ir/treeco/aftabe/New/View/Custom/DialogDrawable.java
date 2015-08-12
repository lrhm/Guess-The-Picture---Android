package ir.treeco.aftabe.New.View.Custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.R;

/**
 * Created by behdad on 8/12/15.
 */

public class DialogDrawable extends Drawable {
    private Paint mPaint;
    private Context mContext;
    private Bitmap dialogTop;
    private Bitmap dialogCenter;
    private Bitmap dialogBottom;
    private int topHeight;
    private int bottomHeight;
    private int topPadding;
    private boolean isDrawable;

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public DialogDrawable(Context mContext) {
        this.mContext = mContext;
        mPaint = new Paint();
        reloadBitmaps(getBounds());
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        reloadBitmaps(bounds);
    }

    private void reloadBitmaps(Rect bounds) {
        if (bounds.height() == 0 || bounds.width() == 0) {
            isDrawable = false;
            return;
        }

        topHeight = MainApplication.lengthManager.getHeightWithFixedWidth(getTopResourceId(), bounds.width());
        dialogTop = ImageManager.loadImageFromResource(mContext, getTopResourceId(), bounds.width(), topHeight);

        int centerHeight = MainApplication.lengthManager.getHeightWithFixedWidth(getCenterResourceId(), bounds.width());
        dialogCenter = ImageManager.loadImageFromResource(mContext, getCenterResourceId(), bounds.width(), centerHeight);

        bottomHeight = MainApplication.lengthManager.getHeightWithFixedWidth(getBottomResourceId(), bounds.width());
        dialogBottom = ImageManager.loadImageFromResource(mContext, getBottomResourceId(), bounds.width(), bottomHeight);

        isDrawable = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isDrawable)
            return;

        canvas.drawBitmap(dialogTop, 0, topPadding, mPaint);
        canvas.drawBitmap(dialogCenter, new Rect(0, 0, dialogCenter.getWidth(), dialogCenter.getHeight()), new Rect(0, topHeight + topPadding, getBounds().width(), getBounds().height() - bottomHeight), mPaint);
        canvas.drawBitmap(dialogBottom, 0, getBounds().height() - bottomHeight, mPaint);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public int getTopResourceId() {
        return R.drawable.dialog_top;
    }

    public int getCenterResourceId() {
        return R.drawable.dialog_center;
    }

    public int getBottomResourceId() {
        return R.drawable.dialog_bottom;
    }
}

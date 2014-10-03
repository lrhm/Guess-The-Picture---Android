package ir.treeco.aftabe;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 9/22/14.
 */
public class DialogDrawable extends Drawable {
    public int getTopResourceId() { return R.drawable.dialog_top; }
    public int getCenterResourceId() { return R.drawable.dialog_center; }
    public int getBottomResourceId() { return R.drawable.dialog_bottom; }

    private final Paint mPaint;
    private final Context mContext;
    private Bitmap dialogTop;
    private Bitmap dialogCenter;
    private Bitmap dialogBottom;
    private int topHeight;
    private int bottomHeight;
    private boolean isDrawable;

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

        topHeight = LengthManager.getHeightWithFixedWidth(getTopResourceId(), bounds.width());
        dialogTop = ImageManager.loadImageFromResource(mContext, getTopResourceId(), bounds.width(), topHeight);

        int centerHeight = LengthManager.getHeightWithFixedWidth(getCenterResourceId(), bounds.width());
        dialogCenter = ImageManager.loadImageFromResource(mContext, getCenterResourceId(), bounds.width(), centerHeight);

        bottomHeight = LengthManager.getHeightWithFixedWidth(getBottomResourceId(), bounds.width());
        dialogBottom = ImageManager.loadImageFromResource(mContext, getBottomResourceId(), bounds.width(), bottomHeight);

        isDrawable = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isDrawable)
            return;

        canvas.drawBitmap(dialogTop, 0, 0, mPaint);
        canvas.drawBitmap(dialogCenter, new Rect(0, 0, dialogCenter.getWidth(), dialogCenter.getHeight()), new Rect(0, topHeight, getBounds().width(), getBounds().height() - bottomHeight), mPaint);
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
}

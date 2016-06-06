package ir.treeco.aftabe2.View.Custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;

public class DialogDrawable extends Drawable {
    private Paint mPaint;
    private Bitmap dialogTop;
    private Bitmap dialogCenter;
    private Bitmap dialogBottom;
    private int topHeight;
    private int bottomHeight;
    private int topPadding;
    private int centerHeight;
    private boolean isDrawable;
    private ImageManager imageManager;
    private LengthManager lengthManager;

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public DialogDrawable(Context context) {
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
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

        topHeight = lengthManager.getHeightWithFixedWidth(getTopResourceId(), bounds.width());
        dialogTop = imageManager.loadImageFromResource(getTopResourceId(), bounds.width(), topHeight);

        centerHeight = lengthManager.getHeightWithFixedWidth(getCenterResourceId(), bounds.width());
        dialogCenter = imageManager.loadImageFromResource(getCenterResourceId(), bounds.width(), centerHeight);

        bottomHeight = lengthManager.getHeightWithFixedWidth(getBottomResourceId(), bounds.width());
        dialogBottom = imageManager.loadImageFromResource(getBottomResourceId(), bounds.width(), bottomHeight);


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

    public int getRightPadding(int height, int width) {

        bottomHeight = lengthManager.getHeightWithFixedWidth(getBottomResourceId(), width);
        centerHeight = lengthManager.getHeightWithFixedWidth(getCenterResourceId(), width);
        topHeight = lengthManager.getHeightWithFixedWidth(getTopResourceId(), width);

        while (centerHeight + bottomHeight + topHeight + topPadding > height) {
            height += 5;
        }
        return height;
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

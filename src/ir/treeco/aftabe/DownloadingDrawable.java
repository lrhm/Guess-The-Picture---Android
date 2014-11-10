package ir.treeco.aftabe;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * Created by hamed on 9/22/14.
 */
public class DownloadingDrawable extends Drawable {
    private final Paint desaturatedPaint;
    private final Paint saturatedPaint;
    Rect desaturatedRect;
    Rect saturatedRect;
    private final Bitmap bitmap;
    int percentage;

    public DownloadingDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;

        saturatedPaint = new Paint();

        desaturatedPaint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        desaturatedPaint.setColorFilter(colorFilter);

        setPercentage(100);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int desaturationHeight = bitmap.getHeight() * (100  - percentage) / 100;
        desaturatedRect = new Rect(0, 0, getBounds().width(), desaturationHeight);
        saturatedRect = new Rect(0, desaturationHeight, getBounds().width(), getBounds().height());
    }

    @Override
    public void draw(Canvas canvas) {
        if (desaturatedRect == null || saturatedRect == null)
            return;

        canvas.drawBitmap(bitmap, desaturatedRect, desaturatedRect, desaturatedPaint);
        canvas.drawBitmap(bitmap, saturatedRect, saturatedRect, saturatedPaint);
    }

    @Override
    public void setAlpha(int i) {
        desaturatedPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        desaturatedPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
        onBoundsChange(getBounds());
    }
}

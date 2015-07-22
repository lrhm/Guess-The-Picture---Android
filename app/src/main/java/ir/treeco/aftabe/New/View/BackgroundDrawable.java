package ir.treeco.aftabe.New.View;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.R;

public class BackgroundDrawable extends GradientDrawable {
    Bitmap background;
    private Paint paint;
    private Rect srcRect;
    private Rect dstRect;

    public BackgroundDrawable(Context mContext, int[] colors) {
        super(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        this.mutate();
        this.setGradientRadius(MainApplication.lengthManager.getHeaderHeight() * 3);
        this.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        this.setGradientCenter(0.5F, 0.5F);

        paint = new Paint();
        paint.setAlpha(30);
        background = ImageManager.loadImageFromResource(mContext, R.drawable.circles, MainApplication.lengthManager.getScreenWidth() / 2, MainApplication.lengthManager.getScreenHeight() / 2, ImageManager.ScalingLogic.ALL_TOP);

        srcRect = new Rect(0, 0, background.getWidth(), background.getHeight());
        dstRect = new Rect(0, 0, background.getWidth() * 2, background.getHeight() * 2);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawBitmap(background, srcRect, dstRect, paint);
        invalidateSelf();
    }
}

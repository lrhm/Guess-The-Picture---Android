package ir.treeco.aftabe.View.Custom;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.LengthManager;

public class BackgroundDrawable extends GradientDrawable {
    private Bitmap background;
    private Paint paint;
    private Rect srcRect;
    private Rect dstRect;
    private ImageManager imageManager;
    private LengthManager lengthManager;

    public BackgroundDrawable(Context context, int[] colors) {
        super(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
        this.mutate();
        this.setGradientRadius(lengthManager.getHeaderHeight() * 3);
        this.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        this.setGradientCenter(0.5F, 0.5F);

        paint = new Paint();
        paint.setAlpha(30);
        background = imageManager.loadImageFromResource(R.drawable.circles, lengthManager.getScreenWidth() / 2, lengthManager.getScreenHeight() / 2, ImageManager.ScalingLogic.ALL_TOP);

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

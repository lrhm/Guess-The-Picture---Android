package ir.treeco.aftabe;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;

public class BackgroundDrawable extends GradientDrawable {
    Bitmap background;
    private Paint paint;
    private Rect srcRect;
    private Rect dstRect;

    public BackgroundDrawable(Context mContext, int[] colors) {
        super(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        this.mutate();
        this.setGradientRadius(LengthManager.getHeaderHeight() * 3);
        this.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        this.setGradientCenter(0.5F, 0.5F);

        paint = new Paint();
        paint.setAlpha(30);
        background = ImageManager.loadImageFromResource(mContext, R.drawable.circles, LengthManager.getScreenWidth() / 2, LengthManager.getScreenHeight() / 2, ImageManager.ScalingLogic.ALL_TOP);

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

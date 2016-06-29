package ir.treeco.aftabe2.View.Custom;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;

import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.LengthManager;

public class BackgroundDrawable extends GradientDrawable {
    private Bitmap background;
    private Paint paint;
    private Rect srcRect;
    private Rect dstRect;
    private ImageManager imageManager;
    private LengthManager lengthManager;

    public BackgroundDrawable(Context context, int[] colors, int drawable) {
        super(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
        this.mutate();
        this.setGradientRadius(lengthManager.getHeaderHeight() * 3);
        this.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        this.setGradientCenter(0.5F, 0.5F);

        paint = new Paint();
        paint.setAlpha(30);

        int width = lengthManager.getScreenWidth();
        int height = lengthManager.getScreenHeight();

        float scale = 0.5f;
        if (imageManager.getMemoryClass() < 100) {
            scale = 0.4f;
        }

        width *= scale;
        height *= scale;

        background = imageManager.loadImageFromResource(drawable, width, height, ImageManager.ScalingLogic.ALL_TOP);

        srcRect = new Rect(0, 0, background.getWidth(), background.getHeight());
        dstRect = new Rect(0, 0, (int) (background.getWidth() / scale), (int) (background.getHeight() / scale));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawBitmap(background, srcRect, dstRect, paint);
        invalidateSelf();
    }
}

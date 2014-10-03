package ir.treeco.aftabe;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * Created by hamed on 9/26/14.
 */
public class RoundedCornerDrawable extends Drawable {
    private final Paint paint;

    public RoundedCornerDrawable() {
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        final float rectRadius = getBounds().height() / 4;
        final float padding = getBounds().height() / 8;
        canvas.drawRoundRect(new RectF(padding, padding, getBounds().width() - padding, getBounds().height() - padding), rectRadius, rectRadius, paint);
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}

package ir.treeco.aftabe;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import ir.treeco.aftabe.utils.FontsHolder;

/**
 * Created by hamed on 11/21/14.
 */
public class LetterButtonDrawable extends Drawable {
    private String label;
    private final Paint paint;
    private int labelX;
    private int labelY;
    private boolean isGreen;

    public LetterButtonDrawable(String label, Context context) {
        this.label = label;
        this.paint = new Paint();

        paint.setTypeface(FontsHolder.getYekan(context));
        paint.setAntiAlias(true);
        setGreen(false);
    }

    @Override
    public void draw(Canvas canvas) {
        if (label != null) {
            canvas.drawText(label, labelX, labelY, paint);
        }
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void setGreen(boolean isGreen) {
        this.isGreen = isGreen;
        if (isGreen)
            paint.setColor(Color.parseColor("#00AA00"));
        else
            paint.setColor(Color.parseColor("#666666"));
        invalidateSelf();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);


        if (label != null) {
            paint.setTextSize(bounds.width() * 6 / 10);

            Rect rectText = new Rect();
            paint.getTextBounds(label, 0, label.length(), rectText);

            labelX = (int) ((bounds.width() - rectText.width()) * 0.5);
            labelY = (int) ((bounds.height() + rectText.height()) * ("جچحخغع".contains(label)? 0.4: 0.5));
        }
    }

    public void setLabel(String label) {
        this.label = label;
        onBoundsChange(getBounds());
        invalidateSelf();
    }

}

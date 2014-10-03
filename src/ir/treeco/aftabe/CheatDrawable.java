package ir.treeco.aftabe;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import ir.treeco.aftabe.utils.FontsHolder;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 9/26/14.
 */
public class CheatDrawable extends Drawable {
    private final Bitmap background;
    private final String title;
    private final String price;
    private final boolean rotated;
    private final Matrix flipHorizontalMatrix;
    private final Paint textPaint;
    private final Context mContext;
    int[] titlePosition = new int[2];
    int[] pricePosition = new int[2];
    private Paint paint;

    public CheatDrawable(Context mContext, int index, Bitmap background, String title, String price) {
        this.background = background;
        this.title = title;
        this.price = price;
        this.rotated = index == 1;
        this.mContext = mContext;

        paint = new Paint();

        if (rotated) {
            flipHorizontalMatrix = new Matrix();
            flipHorizontalMatrix.setScale(-1, 1);
            flipHorizontalMatrix.postTranslate(background.getWidth(), 0);
        } else {
            flipHorizontalMatrix = null;
        }

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(LengthManager.getCheatButtonFontSize());
        textPaint.setTypeface(FontsHolder.getHoma(this.mContext));
        textPaint.setColor(Color.WHITE);
        textPaint.setShadowLayer(1, 1, 1, Color.BLACK);

        centerTextAt(title, (int) (0.365 * background.getWidth()), (int) (background.getHeight() * 0.480), textPaint, titlePosition);
        centerTextAt(price, (int) (0.863 * background.getWidth()), (int) (background.getHeight() * 0.500), textPaint, pricePosition);
    }

    private void centerTextAt(String text, int x, int y, Paint paint, int[] textPosition) {
        Rect rectText = new Rect();
        paint.getTextBounds(text, 0, text.length(), rectText);
        textPosition[0] = (rotated? background.getWidth() - x: x) - rectText.width() / 2;
        textPosition[1] = y + rectText.height() / 2;
    }

    @Override
    public void draw(Canvas canvas) {
        if (rotated)
            canvas.drawBitmap(background, flipHorizontalMatrix, paint);
        else
            canvas.drawBitmap(background, 0, 0, paint);

        canvas.drawText(title, titlePosition[0], titlePosition[1], textPaint);
        canvas.drawText(price, pricePosition[0], pricePosition[1], textPaint);
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

package ir.treeco.aftabe2.View.Custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;

public class CheatDrawable extends Drawable {
    private Bitmap background;
    private String title;
    private String price;
    private boolean rotated;
    private Paint textPaint;
    private Context context;
    private int[] titlePosition = new int[2];
    private int[] pricePosition = new int[2];
    private Paint paint;
    private ImageManager imageManager;
    private LengthManager lengthManager;

    public CheatDrawable(Context context, int index, String title, String price) {
        this.title = title;
        this.price = price;
        this.rotated = index == 1;
        this.context = context;
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();

        background = imageManager.loadImageFromResource(
                R.drawable.cheat_right,
                lengthManager.getCheatButtonWidth(),
                -1);


        paint = new Paint();

        if (rotated) {
            background = flip(background);
        }

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(lengthManager.getCheatButtonFontSize());
        textPaint.setTypeface(FontsHolder.getSansBold(this.context));
        textPaint.setColor(Color.WHITE);
        textPaint.setShadowLayer(1, 1, 1, Color.BLACK);

        centerTextAt(title, (int) (0.375 * background.getWidth()), (int) (background.getHeight() * 0.420), textPaint, titlePosition);
        centerTextAt(price, (int) (0.863 * background.getWidth()), (int) (background.getHeight() * 0.480), textPaint, pricePosition);
    }

    private void centerTextAt(String text, int x, int y, Paint paint, int[] textPosition) {
        Rect rectText = new Rect();
        paint.getTextBounds(text, 0, text.length(), rectText);
        textPosition[0] = (rotated? background.getWidth() - x: x) - rectText.width() / 2;
        textPosition[1] = y + rectText.height() / 2;
    }

    @Override
    public void draw(Canvas canvas) {
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

    public Bitmap flip(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}

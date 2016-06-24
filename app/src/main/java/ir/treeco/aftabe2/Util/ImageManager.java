package ir.treeco.aftabe2.Util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.InputStream;

import ir.treeco.aftabe2.MainApplication;

public class ImageManager {
    private Context context;
    private LengthManager lengthManager;
    private int memoryClass;

    private static boolean cacheInited = false;
    private static Object getLock = new Object();
    private static ImageManager instance;

    public static ImageManager getInstance(Context context) {
        synchronized (getLock) {
            if (instance == null)
                instance = new ImageManager(context);
            return instance;
        }
    }

    private ImageManager(Context context) {


        initCache(context);
        this.context = context;
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();

    }

    public int getMemoryClass() {
        return memoryClass;
    }

    private void initCache(Context context) {

        if (cacheInited)
            return;
        cacheInited = true;

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        memoryClass = am.getMemoryClass();

        int max = (int) ((memoryClass * 1024 * 1024) * ((memoryClass > 100) ? 0.80
                : (0.666))); // more than 50%
        cache = new LruCache<ImageKey, Bitmap>(max) {
            @Override
            protected int sizeOf(ImageKey key, Bitmap value) {
                if (Build.VERSION.SDK_INT >= 12) {
                    return value.getByteCount();
                } else {

                    return value.getRowBytes() * value.getHeight();
                }
            }
        };

    }

    static LruCache<ImageKey, Bitmap> cache;

    public Bitmap loadImageFromResource(int resourceId, int outWidth, int outHeight, ScalingLogic scalingLogic, Bitmap.Config config) {
        if (outWidth == -1) outWidth = lengthManager.getWidthWithFixedHeight(resourceId, outHeight);
        if (outHeight == -1)
            outHeight = lengthManager.getHeightWithFixedWidth(resourceId, outWidth);

        ImageKey key = new ImageKey(resourceId, outWidth, outHeight);

        Bitmap him = cache.get(key);
        if (him != null && !him.isRecycled())
            return him;

        System.gc();

        Bitmap scaledBitmap;
        Bitmap unscaledBitmap;

        unscaledBitmap = decodeFile(resourceId, outWidth, outHeight, scalingLogic, context.getResources());

        scaledBitmap = createScaledBitmap(unscaledBitmap, outWidth, outHeight, scalingLogic, config);
        if (!unscaledBitmap.isRecycled()) unscaledBitmap.recycle();

        cache.put(key, scaledBitmap);
        return scaledBitmap;
    }


    public Bitmap loadImageFromFilse(String path, int outWidth, int outHeight, ScalingLogic scalingLogic) {


        ImageKey key = new ImageKey(path, outWidth, outHeight);

        Bitmap him = cache.get(key);
        if (him != null && !him.isRecycled())
            return him;

        System.gc();

        Bitmap scaledBitmap;
        Bitmap unscaledBitmap;

        unscaledBitmap = decodeFile(path, outWidth, outHeight, scalingLogic, context.getResources());
        scaledBitmap = createScaledBitmap(unscaledBitmap, outWidth, outHeight, scalingLogic, Bitmap.Config.ARGB_8888);
        if (!unscaledBitmap.isRecycled()) unscaledBitmap.recycle();

        cache.put(key, scaledBitmap);
        return scaledBitmap;
    }


    public Bitmap loadImageFromResourceNoCache(int resourceId, int outWidth, int outHeight, ScalingLogic scalingLogic, Bitmap.Config config) {

        return loadImageFromResource(resourceId, outWidth, outHeight, scalingLogic, config);

    }

    public Bitmap loadImageFromResourceNoCache(int resourceId, int outWidth, int outHeight, ScalingLogic scalingLogic) {

        return loadImageFromResource(resourceId, outWidth, outHeight, scalingLogic, Bitmap.Config.ARGB_8888);

    }
    public Bitmap loadImageFromResource(int resourceId, int outWidth, int outHeight, ScalingLogic scalingLogic) {

        return loadImageFromResource(resourceId, outWidth, outHeight, scalingLogic, Bitmap.Config.ARGB_8888);

    }

    public Bitmap loadImageFromResource(int resourceId, int outWidth, int outHeight) {
        return loadImageFromResource(resourceId, outWidth, outHeight, ScalingLogic.CROP, Bitmap.Config.ARGB_8888);
    }


    public Bitmap loadImageFromResource(int resourceId, int outWidth, int outHeight, Bitmap.Config config) {
        return loadImageFromResource(resourceId, outWidth, outHeight, ScalingLogic.CROP, config);
    }


    public Bitmap loadImageFromInputStream(InputStream inputStream, int outWidth, int outHeight) {
        System.gc();

        Bitmap scaledBitmap;
        Bitmap unscaledBitmap;

        if (inputStream == null)
            throw new IllegalStateException("null InputStream!");

        unscaledBitmap = decodeInputStream(inputStream);

        if (outHeight == -1)
            outHeight = unscaledBitmap.getHeight() * outWidth / unscaledBitmap.getWidth();

        if (outWidth == -1)
            outWidth = unscaledBitmap.getWidth() * outHeight / unscaledBitmap.getHeight();

        scaledBitmap = createScaledBitmap(unscaledBitmap, outWidth, outHeight, ScalingLogic.CROP, Bitmap.Config.ARGB_8888);

        if (!unscaledBitmap.isRecycled()) unscaledBitmap.recycle();

        return scaledBitmap;
    }

    public enum ScalingLogic {FIT, CROP, ALL_TOP}

    public Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        switch (scalingLogic) {
            case CROP:
                final float srcAspect = (float) srcWidth / (float) srcHeight;
                final float dstAspect = (float) dstWidth / (float) dstHeight;
                if (srcAspect > dstAspect) {
                    final int srcRectWidth = (int) (srcHeight * dstAspect);
                    final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                    return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
                } else {
                    final int srcRectHeight = (int) (srcWidth / dstAspect);
                    final int scrRectTop = (srcHeight - srcRectHeight) / 2;
                    return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
                }
            case FIT:
                return new Rect(0, 0, srcWidth, srcHeight);
            case ALL_TOP:
                return new Rect(0, 0, srcWidth, Math.min(srcHeight, dstHeight * srcWidth / dstWidth));
            default:
                return null;
        }
    }

    public Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        switch (scalingLogic) {
            case FIT:
                final float srcAspect = (float) srcWidth / (float) srcHeight;
                final float dstAspect = (float) dstWidth / (float) dstHeight;
                if (srcAspect > dstAspect)
                    return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
                else
                    return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
            case CROP:
                return new Rect(0, 0, dstWidth, dstHeight);
            case ALL_TOP:
                return new Rect(0, 0, dstWidth, Math.min(dstHeight, srcHeight * dstWidth / srcWidth));
            default:
                return null;
        }
    }

    public Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic, Bitmap.Config config) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), config);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public Bitmap decodeFile(int resourceId, int dstWidth, int dstHeight, ScalingLogic scalingLogic, Resources resources) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(resources, resourceId, options);
    }

    public Bitmap decodeFile(String file, int dstWidth, int dstHeight, ScalingLogic scalingLogic, Resources resources) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeFile(file, options);
    }

    public Bitmap decodeInputStream(InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public void toGrayscale(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

    public void toNormalscale(ImageView imageView) {

        ColorMatrix matrix = new ColorMatrix();

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

}

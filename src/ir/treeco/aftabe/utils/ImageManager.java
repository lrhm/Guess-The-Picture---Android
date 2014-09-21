package ir.treeco.aftabe.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.support.v4.util.LruCache;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: hamed
 * Date: 3/8/14
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */

class ImageKey {
    String data;

    ImageKey(int resourceId, int width, int height) {
        data = "_" + resourceId + "," + width + "," + height;
    }

    ImageKey(String relativePath, int width, int height) {
        data = "@" + relativePath + "," + width + "," + height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageKey imageKey = (ImageKey) o;

        if (data != null ? !data.equals(imageKey.data) : imageKey.data != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }


    @Override
    public String toString() {
        return data;
    }
}

public class ImageManager {
    static LruCache<ImageKey, Bitmap> cache = new LruCache<ImageKey, Bitmap>(1) {
        @Override
        protected int sizeOf(ImageKey key, Bitmap value) {
            return 0;
        }
    };

    public static Bitmap loadImageFromResource(Context activity, int resourceId, int outWidth, int outHeight) {
        if (outWidth == -1) outWidth = LengthManager.getWidthWithFixedHeight(resourceId, outHeight);
        if (outHeight == -1) outHeight = LengthManager.getHeightWithFixedWidth(resourceId, outWidth);

        ImageKey key = new ImageKey(resourceId, outWidth, outHeight);

        Bitmap him = cache.get(key);
        if (him != null && !him.isRecycled())
            return him;

        System.gc();

        Bitmap unscaledBitmap = decodeFile(resourceId, outWidth, outHeight, ScalingLogic.CROP, activity.getResources());
        Bitmap scaledBitmap = createScaledBitmap(unscaledBitmap, outWidth, outHeight, ScalingLogic.CROP);
        if (!unscaledBitmap.isRecycled()) unscaledBitmap.recycle();

        cache.put(key, scaledBitmap);
        return scaledBitmap;
    }

    public static Bitmap loadImageFromInputStream(InputStream inputStream, int outWidth, int outHeight) {
        Bitmap unscaledBitmap = decodeInputStream(inputStream);
        if(outHeight == -1)
            outHeight = unscaledBitmap.getHeight()*outWidth/unscaledBitmap.getWidth();
        if(outWidth == -1)
            outWidth = unscaledBitmap.getWidth()*outHeight/unscaledBitmap.getHeight();
        Bitmap scaledBitmap = createScaledBitmap(unscaledBitmap, outWidth, outHeight, ScalingLogic.CROP);
        if (!unscaledBitmap.isRecycled()) unscaledBitmap.recycle();

        return scaledBitmap;
    }

    enum ScalingLogic {FIT, CROP}

    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int
            dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;
            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int)(srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int)(srcWidth / dstAspect);
                final int scrRectTop = (int)(srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int
            dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;
            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int)(dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int)(dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int
            dstHeight, ScalingLogic scalingLogic) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(),
                unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(),
                unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new
                Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public static Bitmap decodeFile(int resourceId, int dstWidth, int dstHeight,
                                    ScalingLogic scalingLogic, Resources resources) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(resources, resourceId, options);
        //options.inJustDecodeBounds = false;
        options.inScaled = false;
        //options.inSampleSize = //calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight, scalingLogic);
        return BitmapFactory.decodeResource(resources, resourceId, options);
    }

    public static Bitmap decodeInputStream(InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    /* public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int
            dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;
            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;
            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    } */
}

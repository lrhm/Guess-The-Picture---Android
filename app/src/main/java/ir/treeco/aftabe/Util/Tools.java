package ir.treeco.aftabe.Util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.UnsupportedEncodingException;

public class Tools {

    public String decodeBase64(String string) {
        byte[] data = Base64.decode(string, Base64.DEFAULT);
        String solution = "";

        try {
            solution = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return solution;

    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }


    public static Bitmap updateHSV(Bitmap src, float settingHue, float settingSat, float settingVal) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];

        float[] pixelHSV = new float[3];

        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {

                // Convert from Color to HSV
                Color.colorToHSV(mapSrcColor[index], pixelHSV);
                int alpha = Color.alpha(mapSrcColor[index]);

                // Adjust HSV
                pixelHSV[0] = pixelHSV[0] + settingHue;
                if (pixelHSV[0] < 0.0f) {
                    pixelHSV[0] += 360;
                } else if (pixelHSV[0] > 360.0f) {
                    pixelHSV[0] -= 360.0f;
                }

                pixelHSV[1] = pixelHSV[1] + settingSat;
                if (pixelHSV[1] < 0.0f) {
                    pixelHSV[1] = 0.0f;
                } else if (pixelHSV[1] > 1.0f) {
                    pixelHSV[1] = 1.0f;
                }

                pixelHSV[2] = pixelHSV[2] + settingVal;
                if (pixelHSV[2] < 0.0f) {
                    pixelHSV[2] = 0.0f;
                } else if (pixelHSV[2] > 1.0f) {
                    pixelHSV[2] = 1.0f;
                }

                // Convert back from HSV to Color
                mapDestColor[index] = Color.HSVToColor(alpha, pixelHSV);

                index++;
            }
        }

        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);

    }

    public void resizeView(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (width != layoutParams.width || height != layoutParams.height) {
            layoutParams.width = width;
            layoutParams.height = height;
        }
    }


    public void reverseLinearLayout(LinearLayout linearLayout) {
        View views[] = new View[linearLayout.getChildCount()];
        for (int i = 0; i < views.length; i++)
            views[i] = linearLayout.getChildAt(i);
        linearLayout.removeAllViews();
        for (int i = views.length - 1; i >= 0; i--)
            linearLayout.addView(views[i]);
    }

    public String numeralStringToPersianDigits(String s) {
        String persianDigits = "۰۱۲۳۴۵۶۷۸۹";
        char[] result = new char[s.length()];
        for (int i = 0; i < s.length(); i++)
            result[i] = Character.isDigit(s.charAt(i)) ? persianDigits.charAt(s.charAt(i) - '0') : s.charAt(i);
        return new String(result);
    }

    public void setViewBackground(final View view, Drawable dialogDrawable) {
        if (Build.VERSION.SDK_INT >= 16)
            view.setBackground(dialogDrawable);
        else {
            view.setBackgroundDrawable(dialogDrawable);
        }
    }
}

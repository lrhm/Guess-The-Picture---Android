package ir.treeco.aftabe.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hamed on 9/29/14.
 */
public class ToastMaker {
    public static void show(Context context, String content, int duration) {
        TextView textView = new TextView(context);
        Utils.setViewBackground(textView, new ToastBackgroundDrawable(context));
        textView.setText(content);
        textView.setTypeface(FontsHolder.getHoma(context));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getToastFontSize());
        textView.setShadowLayer(1, 1, 1, Color.BLACK);
        textView.setTextColor(Color.WHITE);
        textView.setLayoutParams(new ViewGroup.LayoutParams(LengthManager.getToastWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(textView);
        toast.show();
    }
}

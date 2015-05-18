package ir.treeco.aftabe.View.Toast;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ir.treeco.aftabe.View.Toast.ToastBackgroundDrawable;
import ir.treeco.aftabe.utils.FontsHolder;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.Utils;

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
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(LengthManager.getToastWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        final int padding = LengthManager.getToastPadding();
        textView.setPadding(padding, padding, padding, padding);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(textView);
        toast.show();
    }
}

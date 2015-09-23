package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.Tools;

public class ToastMaker {
    public static void show(Context context, String content, int duration) {
        Tools tools = new Tools();
        TextView textView = new TextView(context);
        tools.setViewBackground(textView, new ToastBackgroundDrawable());
        textView.setText(content);
        textView.setTypeface(FontsHolder.getHoma(context));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainApplication.lengthManager.getToastFontSize());
        textView.setShadowLayer(1, 1, 1, Color.BLACK);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(MainApplication.lengthManager.getToastWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        int padding = MainApplication.lengthManager.getToastPadding();
        textView.setPadding(padding, padding, padding, padding);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(textView);
        toast.show();
    }
}

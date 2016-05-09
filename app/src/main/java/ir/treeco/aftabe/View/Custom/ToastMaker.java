package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.Tools;

public class ToastMaker {
    public static void show(Context context, String content, int duration) {

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            Toast.makeText(context, content, duration).show();
            return;
        }

        Tools tools = new Tools(context);
        LengthManager lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
        TextView textView = new TextView(context);
        tools.setViewBackground(textView, new ToastBackgroundDrawable(context));
        textView.setText(content);
        textView.setTypeface(FontsHolder.getSansMedium(context));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, lengthManager.getToastFontSize());
        textView.setShadowLayer(1, 1, 1, Color.BLACK);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(lengthManager.getToastWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        int padding = lengthManager.getToastPadding();
        textView.setPadding(padding, padding, padding, padding);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(textView);
        toast.show();
    }
}

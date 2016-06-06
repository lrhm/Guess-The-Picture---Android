package ir.treeco.aftabe2.View.Custom;

import android.content.Context;
import android.graphics.Color;
import android.telephony.TelephonyManager;

import ir.treeco.aftabe2.Util.Logger;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.Util.UiUtil;

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
        ToastBackgroundDrawable toastBackgroundDrawable = new ToastBackgroundDrawable(context);
        tools.setViewBackground(textView, toastBackgroundDrawable);
        textView.setText(content);
        textView.setTypeface(FontsHolder.getSansMedium(context));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, lengthManager.getToastFontSize() * 0.8f);
        textView.setShadowLayer(1, 1, 1, Color.BLACK);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(lengthManager.getToastWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        int padding = (int) (lengthManager.getToastPadding() * 0.7);


        textView.setPadding(padding, padding / 2, padding, padding / 2);

        int textSize = UiUtil.getTextViewHeight(textView);
        int rightPadding = toastBackgroundDrawable.getRightPadding(textSize + padding, UiUtil.getTextViewWidth(textView) + 2 * padding) -textSize;
        Logger.d("TEST", "right padding is " + rightPadding + " padding is + " + padding);
        textView.setPadding(padding, rightPadding/2, padding, rightPadding/2);


        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, (int) (SizeManager.getScreenHeight() * 0.05));
        toast.setDuration(duration);
        toast.setView(textView);
        toast.show();
    }
}

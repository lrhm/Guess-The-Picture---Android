package ir.treeco.aftabe2.View.Custom;

import android.content.Context;

import ir.treeco.aftabe2.R;

public class ToastBackgroundDrawable extends DialogDrawable {
    @Override
    public int getTopResourceId() {
        return R.drawable.toast_top;
    }

    @Override
    public int getCenterResourceId() {
        return R.drawable.toast_center;
    }

    @Override
    public int getBottomResourceId() {
        return R.drawable.toast_buttom;
    }

    public ToastBackgroundDrawable(Context context) {
        super(context);
    }
}

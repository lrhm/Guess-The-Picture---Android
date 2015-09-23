package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import ir.treeco.aftabe.R;

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

    public ToastBackgroundDrawable(Context mContext) {
        super(mContext);
    }
}

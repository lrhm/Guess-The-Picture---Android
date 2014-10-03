package ir.treeco.aftabe.utils;

import android.content.Context;
import ir.treeco.aftabe.DialogDrawable;
import ir.treeco.aftabe.R;

/**
 * Created by hamed on 9/29/14.
 */
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

package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;

/**
 * TODO: document your custom view class.
 */
public class UserLevelMarkView extends RelativeLayout {
    private int mUserMark;
    private int mUserExp;
    private final int markCount = 8;
    private ImageView expView;
    private ImageView baseView;
    private ImageView coverView;
    private LengthManager lengthManager;
    private ImageManager imageManager;

    public UserLevelMarkView(Context context, int userMark, int userExp) {
        super(context);
        mUserMark = userMark;
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();

        baseView = new ImageView(context);
        baseView.setImageBitmap(imageManager.loadImageFromResource((R.drawable.base),
                (int) (lengthManager.getScreenWidth() * (0.8d)), (int) (lengthManager.getScreenWidth() * (0.8d)) , ImageManager.ScalingLogic.CROP));


        coverView = new ImageView(context);
        coverView.setImageBitmap(imageManager.loadImageFromResource((R.drawable.cover),
                (int) (lengthManager.getScreenWidth() * (0.8d)), (int) (lengthManager.getScreenWidth() * (0.8d)), ImageManager.ScalingLogic.CROP));

        expView = new ImageView(context);
        setUserExp(userExp);

        addView(baseView);
        addView(expView);
        addView(coverView);

    }

    public void setUserExp(int userExp) {
        mUserExp = userExp;
        expView.setImageBitmap(imageManager.loadImageFromResource(getExpID(mUserExp),
                (int) (lengthManager.getScreenWidth() * (0.8d)), (int) (lengthManager.getScreenWidth() * (0.8d)), ImageManager.ScalingLogic.CROP));

    }

    public void setmUserMark(int userMark) {
        mUserMark = userMark;
    }

    private int getExpID(int expLevel) {
        switch (expLevel) {
            case 1:
                return R.drawable.exp1;
            case 2:
                return R.drawable.exp2;
            case 3:
                return R.drawable.exp3;
            case 4:
                return R.drawable.exp4;
            case 5:
                return R.drawable.exp5;
            case 6:
                return R.drawable.exp6;
            case 7:
                return R.drawable.exp7;
            case 8:
                return R.drawable.exp8;



        }   return 0;
    }


}

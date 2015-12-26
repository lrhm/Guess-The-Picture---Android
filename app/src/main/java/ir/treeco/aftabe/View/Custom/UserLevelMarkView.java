package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
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
    private float mDimension;


    private boolean isUser;
    private final int maxMarkCount = 8;
    private ImageView expView;
    private ImageView baseView;
    private ImageView coverView;

    private LengthManager lengthManager;
    private ImageManager imageManager;


    public UserLevelMarkView(Context context) {

        super(context);
        init(context, null, 0);
        mUserLevelMarkView(context);
    }

    public UserLevelMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
        mUserLevelMarkView(context);
    }

    public UserLevelMarkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs, defStyle);
        mUserLevelMarkView(context);
    }

    private void mUserLevelMarkView(Context context) {
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();

        baseView = new ImageView(context);
        baseView.setImageBitmap(imageManager.loadImageFromResource((R.drawable.base),
                (int) (lengthManager.getScreenWidth() * (mDimension)), (int) (lengthManager.getScreenWidth() * (mDimension)), ImageManager.ScalingLogic.CROP));


        coverView = new ImageView(context);
        coverView.setImageBitmap(imageManager.loadImageFromResource((R.drawable.cover),
                (int) (lengthManager.getScreenWidth() * (mDimension)), (int) (lengthManager.getScreenWidth() * (mDimension)), ImageManager.ScalingLogic.CROP));


        expView = new ImageView(context);

        addView(baseView);
        addView(expView);
        addView(coverView);

        if (!isUser)
            setUserExp(10);
        else
            setUserExp(3);
//        TODO remove the line above
    }

    public boolean isUser() {
        return isUser;
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.UserLevelMarkView, defStyle, 0);
        mDimension = a.getFloat(0, 0.5f);
        if (a.hasValue(1)) {
            isUser = a.getBoolean(1, true);
        } else
            isUser = true;

        a.recycle();


    }

    public void setUserExp(int userExp) {
        mUserExp = userExp;
        expView.setImageBitmap(imageManager.loadImageFromResource(getExpID(mUserExp),
                (int) (lengthManager.getScreenWidth() * (mDimension)), (int) (lengthManager.getScreenWidth() * (mDimension)), ImageManager.ScalingLogic.CROP));

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
            case 10:
                return R.drawable.avatarcover;


        }
        return 0;
    }


}

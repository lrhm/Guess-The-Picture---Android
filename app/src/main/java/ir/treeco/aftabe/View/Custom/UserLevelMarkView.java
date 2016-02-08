package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Dialog.ImageFullScreenDialog;
import ir.treeco.aftabe.View.Dialog.LeaderboardDialog;
import ir.treeco.aftabe.View.Dialog.UserViewDialog;

/**
 * TODO: document your custom view class.
 */
public class UserLevelMarkView extends LinearLayout {
    private int mUserMark;
    private int mUserExp;
    private float mDimension;


    private static final int TEXT_ALIGN_LEFT = 0;
    private static final int TEXT_ALIGN_BELOW = 1;
    private static final int TEXT_ALIGN_CENTER = 2;

    private User mUser;
    private int mTextAlign;
    private boolean isUser;
    private final int maxMarkCount = 8;
    private ImageView expView;
    private ImageView baseView;
    private ImageView coverView;
    private TextView mUserNameTextView;
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


    private void mUserLevelMarkView(Context context) {
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();


        FrameLayout imagesContainer = new FrameLayout(context);


        baseView = new ImageView(context);
        baseView.setImageBitmap(imageManager.loadImageFromResource((R.drawable.base),
                (int) (lengthManager.getScreenWidth() * (mDimension)), (int) (lengthManager.getScreenWidth() * (mDimension)), ImageManager.ScalingLogic.FIT));


        coverView = new ImageView(context);
        coverView.setImageBitmap(imageManager.loadImageFromResource((R.drawable.cover),
                (int) (lengthManager.getScreenWidth() * (mDimension)), (int) (lengthManager.getScreenWidth() * (mDimension)), ImageManager.ScalingLogic.FIT));


        mUserNameTextView = new TextView(context);
        mUserNameTextView.setText("اسمته");
        mUserNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mDimension * 80);

        LayoutParams textLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textLP.gravity = Gravity.CENTER;
        imagesContainer.setLayoutParams(textLP);

        expView = new ImageView(context);


        imagesContainer.addView(baseView);
        imagesContainer.addView(expView);
        imagesContainer.addView(coverView);

        int orientation = VERTICAL;
        if (mTextAlign == TEXT_ALIGN_LEFT)
            orientation = HORIZONTAL;

        setOrientation(orientation);

        if (mTextAlign != TEXT_ALIGN_CENTER) {

            if (orientation == VERTICAL) {
                textLP.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
                mUserNameTextView.setLayoutParams(textLP);

                addView(imagesContainer);
                addView(mUserNameTextView);
            } else {
                textLP.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
                mUserNameTextView.setLayoutParams(textLP);

                addView(mUserNameTextView);
                addView(imagesContainer);
            }
        } else { //Text Align Center
            imagesContainer.addView(mUserNameTextView);

            mUserNameTextView.setText(mUserNameTextView.getText().subSequence(0, 2));
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mUserNameTextView.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            mUserNameTextView.setGravity(Gravity.CENTER);

            addView(imagesContainer);
        }

        FontsHolder.setFont(mUserNameTextView, FontsHolder.SANS_REGULAR);

        if (!isUser)
            setUserExp(10);
        else
            setUserExp(3);


        setDefualtListener();

//        TODO remove the line above
    }

    public void setDefualtListener(){
        if (!isClickable()) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isUser) {
                        new UserViewDialog(getContext()).show();
                    } else {
                        new LeaderboardDialog().show(getActivity().getSupportFragmentManager(), "leaderboard");
                    }
                }
            });

        }
    }

    private MainActivity getActivity() {


        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof MainActivity) {
                return (MainActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
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

        if (a.hasValue(2)) {
            mTextAlign = a.getInt(2, 1);
        } else
            mTextAlign = 1;

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

    public void setUser(User user) {
        this.mUser = user;
        setmUserMark(user.getMark());
        setUserExp(user.getRank());
        setUserName(user.getUserName());
    }

    private void setUserName(String userName) {

        if (mTextAlign != TEXT_ALIGN_CENTER) {
            mUserNameTextView.setText(userName);

        } else { //Text Align Center

            mUserNameTextView.setText(userName.subSequence(0, 2));

        }

    }

    public void setUserGuest(){
        mUserNameTextView.setText("عضویت/ورود");
    }

}

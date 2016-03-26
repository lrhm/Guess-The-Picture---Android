package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Dialog.LeaderboardDialog;
import ir.treeco.aftabe.View.Dialog.RegistrationDialog;
import ir.treeco.aftabe.View.Dialog.UserViewDialog;

/**
 * TODO: document your custom view class.
 */
public class UserLevelView extends LinearLayout implements View.OnClickListener {
    private int mUserLevel;
    private int mUserExp;
    private float mDimension;

    public static final int STATE_WIN = 0;
    public static final int STATE_LOSE = 1;
    public static final int STATE_UNKNOWN = 2;

    private static final int TEXT_ALIGN_LEFT = 0;
    private static final int TEXT_ALIGN_BELOW = 1;
    private static final int TEXT_ALIGN_CENTER = 2;

    private boolean mClick = true;

    private User mUser;
    private int mTextAlign;
    private ImageView expView;
    private ImageView stateView;
    private ImageView baseView;
    private ImageView coverView;
    private MagicTextView mUserNameTextView;
    private MagicTextView mLevelTextView;
    private LengthManager lengthManager;
    private ImageManager imageManager;


    public UserLevelView(Context context) {

        super(context);
        init(context, null, 0);
        mUserLevelMarkView(context);
    }

    public UserLevelView(Context context, AttributeSet attrs) {
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


        mUserNameTextView = new MagicTextView(context);
        mUserNameTextView.setText("اسمته");
        mUserNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mDimension * 80);
        mUserNameTextView.setTypeface(FontsHolder.getRiffic(getContext()));
        mUserNameTextView.setTextColor(Color.WHITE);

        setShadowLayer(mUserNameTextView);

        LayoutParams textLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textLP.gravity = Gravity.CENTER;


        mLevelTextView = new MagicTextView(context);
        mLevelTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mDimension * 150);
        mLevelTextView.setTypeface(FontsHolder.getHoma(context));
        mLevelTextView.setTextColor(Color.WHITE);
        mLevelTextView.setGravity(Gravity.CENTER);

//        mLevelTextView.setStroke(strokeSize, Color.BLACK);

        FrameLayout.LayoutParams levelTextViewLP = new FrameLayout.LayoutParams((int) (lengthManager.getScreenWidth() * (mDimension)),
                (int) (lengthManager.getScreenWidth() * (mDimension)));
//        levelTextViewLP.gravity = Gravity.CENTER_VERTICAL;
//        levelTextViewLP.topMargin = -(int) (SizeManager.getScreenHeight() * mDimension * 0.005);

        mLevelTextView.setLayoutParams(levelTextViewLP);
        setShadowLayer(mLevelTextView);


        imagesContainer.setLayoutParams(textLP);

        expView = new ImageView(context);

        stateView = new ImageView(context);

        imagesContainer.addView(baseView);
        imagesContainer.addView(expView);
        imagesContainer.addView(stateView);
        imagesContainer.addView(coverView);
        imagesContainer.addView(mLevelTextView);

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

        setOnClickListener(this);


    }

    public void setShadowLayer(MagicTextView mLevelTextView) {
//        float shadowSize = (mDimension * 6 / (0.7f));
//        Log.d("LevelUserVIew", shadowSize + " is the shadow size");
        float shadowSize = (mDimension / 0.7f) * 4;
        mLevelTextView.addOuterShadow(1, shadowSize, shadowSize, Color.BLACK);
        mLevelTextView.addInnerShadow(1, shadowSize, shadowSize, Color.BLACK);

        int strokeSize = (int) (SizeManager.getScreenWidth() * mDimension / 120);

        mLevelTextView.setStroke(strokeSize, Color.parseColor("#909090"));

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


    private void init(Context context, AttributeSet attrs, int defStyle) {

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.UserLevelView, defStyle, 0);

        if (a.hasValue(R.styleable.UserLevelView_customDimension))
            mDimension = a.getFloat(0, 0.5f);
        if (a.hasValue(R.styleable.UserLevelView_textAlign)) {
            mTextAlign = a.getInt(1, 1);
        } else
            mTextAlign = 1;

        a.recycle();


    }

    public void setUserExp(int userExp) {
        mUserExp = userExp;
        expView.setImageBitmap(imageManager.loadImageFromResource(getExpID(mUserExp),
                (int) (lengthManager.getScreenWidth() * (mDimension)), (int) (lengthManager.getScreenWidth() * (mDimension)), ImageManager.ScalingLogic.CROP));

    }

    public void setUserLevel(int userMark) {
        mUserLevel = userMark;
        mLevelTextView.setText(Tools.numeralStringToPersianDigits(userMark + ""));
    }

    private int getExpID(int expLevel) {
        expLevel++;
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


        }
        return 0;
    }

    public void setUser(User user) {
        this.mUser = user;
        setUserLevel(user.getLevel());
        setUserExp(user.getExp());
        setUserName(user.getName());


    }

    private void setUserName(String userName) {

        if (mTextAlign != TEXT_ALIGN_CENTER) {
            mUserNameTextView.setText(userName);

        } else {
            mUserNameTextView.setText(userName.subSequence(0, 2));

        }

    }

    public void setUserGuest() {
        mUserNameTextView.setText("عضویت/ورود");
    }

    public void setOnlineState(int firstState, int secondState) {
        int width = (int) (SizeManager.getScreenWidth() * mDimension);
        if (firstState == STATE_UNKNOWN) {
            expView.setVisibility(View.GONE);
        } else {
            expView.setVisibility(View.VISIBLE);
            expView.setImageBitmap(imageManager.loadImageFromResource(getDrawableIdForRight(firstState), width, width));

        }

        if (secondState == STATE_UNKNOWN) {
            stateView.setVisibility(View.GONE);
        } else {
            stateView.setVisibility(View.VISIBLE);
            stateView.setImageBitmap(imageManager.loadImageFromResource(getDrawableIdForLeft(secondState), width, width));

        }

    }

    private int getDrawableIdForLeft(int state) {
        if (state == STATE_LOSE)
            return R.drawable.wrong1;
        if (state == STATE_WIN)
            return R.drawable.correct1;
        return 0;
    }

    private int getDrawableIdForRight(int state) {
        if (state == STATE_LOSE)
            return R.drawable.wrong2;
        if (state == STATE_WIN)
            return R.drawable.correct2;
        return 0;
    }

    @Override
    public void onClick(View v) {

        Log.d(this.getClass().getSimpleName(), "on click");

        if (!mClick)
            return;

        if (Tools.isUserRegistered()) {


            if (mUser == null)
                return;
            if (!mUser.isMe()) {
                new UserViewDialog(getContext(), mUser).show();
            } else {
                new LeaderboardDialog().show(getActivity().getSupportFragmentManager(), "leaderboard");
            }

            return;
        }

        new RegistrationDialog(getContext()).show();


    }

    public boolean isClick() {
        return mClick;
    }

    public void setClick(boolean mClick) {
        this.mClick = mClick;
    }

}

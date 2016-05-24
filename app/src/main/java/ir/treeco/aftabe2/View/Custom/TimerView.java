package ir.treeco.aftabe2.View.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;

/**
 * Created by al on 4/25/16.
 */
public class TimerView extends FrameLayout {

    ImageView container;
    ImageView timer;
    TextView mTextView;
    int mReamingTime;
    SizeConverter mainConverter;
    ImageManager imageManager;
    boolean isTimerBlue = true;
    boolean doOnlyBlue = false;

    public TimerView(Context context) {
        super(context);

        init(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    public void init(Context context) {

        mReamingTime = 120;
        imageManager = ImageManager.getInstance(context);
        container = new ImageView(context);
        timer = new ImageView(context);
        mTextView = new TextView(context);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTypeface(FontsHolder.getNumeralSansMedium(context));
        mainConverter = SizeConverter.SizeConvertorFromWidth((float) (SizeManager.getScreenWidth() * 0.16), 437, 257);

        container.setImageBitmap(imageManager.loadImageFromResource(R.drawable.timer, mainConverter.mWidth, mainConverter.mHeight));
        timer.setImageBitmap(imageManager.loadImageFromResource(R.drawable.timerblue, mainConverter.mWidth, mainConverter.mHeight));
        AlphaAnimation animation1 = new AlphaAnimation(1f, 0f);
        animation1.setDuration(2000);
        animation1.setRepeatMode(Animation.REVERSE);
        animation1.setRepeatCount(Animation.INFINITE);
        timer.startAnimation(animation1);


        addView(container);
        addView(timer);
        addView(mTextView);

    }

    public int getRealWidth(){
        return mainConverter.mWidth;
    }

    public int getRealHeight(){
        return mainConverter.mHeight;
    }

    public void setTimer(int time) {
        mTextView.setText(Tools.numeralStringToPersianDigits(time + ""));

        if (doOnlyBlue) {
            if (!isTimerBlue) {
                timer.setImageBitmap(imageManager.loadImageFromResource(R.drawable.timerblue, mainConverter.mWidth, mainConverter.mHeight));

                isTimerBlue = true;
            }
            return;
        }

        if (time == 30) {
            timer.setImageBitmap(imageManager.loadImageFromResource(R.drawable.timerred, mainConverter.mWidth, mainConverter.mHeight));

            isTimerBlue = false;
        } else if (time > 30 && !isTimerBlue) {
            isTimerBlue = true;
            timer.setImageBitmap(imageManager.loadImageFromResource(R.drawable.timerblue, mainConverter.mWidth, mainConverter.mHeight));
        }

    }

    public void setDoOnlyBlue(boolean doOnlyBlue) {
        this.doOnlyBlue = doOnlyBlue;
    }

}

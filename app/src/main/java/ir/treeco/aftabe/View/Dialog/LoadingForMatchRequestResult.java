package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestSFHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.OnlineFriendStatusHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.API.Socket.SocketFriendMatchListener;
import ir.treeco.aftabe.API.Socket.SocketListener;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.TimerView;
import ir.treeco.aftabe.View.Fragment.GameResultFragment;
import ir.treeco.aftabe.View.Fragment.OnlineGameFragment;


/**
 * Created by al on 3/16/16.
 */
public class LoadingForMatchRequestResult extends Dialog implements Runnable, SocketFriendMatchListener {

    Context context;
    private boolean mDismissed = false;
    private ImageView mLoadingImageView;
    private static int mLoadingImageWidth = 0, mLoadingImageHeight = 0;
    private ImageManager imageManager;
    private static int[] mImageLoadingIds;
    int mLoadingStep = 0;
    private User mOpponent;
    private Timer mTimer;
    TimerView mTimerView;
    int mTimerStep = 0;

    public LoadingForMatchRequestResult(Context context, User opponent) {
        super(context);
        this.context = context;
        mOpponent = opponent;
        imageManager = new ImageManager(context);
        initImageLoading();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runTimer();
            }
        },0 ,1000);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90000000")));

        setContentView(R.layout.dialog_game_result_loading);

        mDismissed = false;
        mLoadingImageView = (ImageView) findViewById(R.id.activity_main_loading_image_view);
        mLoadingImageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(mImageLoadingIds[0],
                mLoadingImageWidth, mLoadingImageHeight, ImageManager.ScalingLogic.FIT));
        new Handler().postDelayed(this, 20);
        SocketAdapter.addFriendSocketListener(this);

        mTimerView = new TimerView(context);
        mTimerView.setDoOnlyBlue(true);

        FrameLayout container = (FrameLayout) findViewById(R.id.dialog_game_result_loading_container);

        FrameLayout.LayoutParams timerLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timerLP.topMargin = (int) (SizeManager.getScreenHeight() * 0.2f);
        timerLP.leftMargin = (SizeManager.getScreenWidth() - mTimerView.getRealWidth()) / 2;
        container.addView(mTimerView, timerLP);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        lp.height = SizeManager.getScreenHeight();
        getWindow().setAttributes(lp);


    }

    private void runTimer() {
        if (mTimerStep == 30) {
            mTimer.cancel();
            dismiss();
        }
        mTimerStep++;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mTimerView.setTimer(30 - mTimerStep);

            }
        });
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void initImageLoading() {


        if (mLoadingImageHeight != 0 && mLoadingImageWidth != 0
                && mImageLoadingIds != null)
            return;
        SizeConverter converter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.3f,
                128, 128);
        mLoadingImageHeight = converter.mHeight;
        mLoadingImageWidth = converter.mWidth;

        int[] idt = {R.drawable.pr_2_00000,
                R.drawable.pr_2_00001,
                R.drawable.pr_2_00002,
                R.drawable.pr_2_00003,
                R.drawable.pr_2_00004,
                R.drawable.pr_2_00005,
                R.drawable.pr_2_00006,
                R.drawable.pr_2_00007,
                R.drawable.pr_2_00008,
                R.drawable.pr_2_00009,
                R.drawable.pr_2_00010,
                R.drawable.pr_2_00011,
                R.drawable.pr_2_00012,
                R.drawable.pr_2_00013,
                R.drawable.pr_2_00014,
                R.drawable.pr_2_00015,
                R.drawable.pr_2_00016,
                R.drawable.pr_2_00017,
                R.drawable.pr_2_00018,
                R.drawable.pr_2_00019,
                R.drawable.pr_2_00020,
                R.drawable.pr_2_00021,
                R.drawable.pr_2_00022,
                R.drawable.pr_2_00023,
                R.drawable.pr_2_00024,
                R.drawable.pr_2_00025,
                R.drawable.pr_2_00026,
                R.drawable.pr_2_00027,
                R.drawable.pr_2_00028,
                R.drawable.pr_2_00029,
                R.drawable.pr_2_00030,
                R.drawable.pr_2_00031,
                R.drawable.pr_2_00032};

        mImageLoadingIds = idt;


    }


    @Override
    public void dismiss() {
        mDismissed = true;
        super.dismiss();
    }

    @Override
    public void run() {

        mLoadingStep++;

        if (mDismissed)
            return;

        if (mLoadingStep == mImageLoadingIds.length) { // the last image
            mLoadingStep = 0;

        }

        mLoadingImageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(mImageLoadingIds[mLoadingStep],
                mLoadingImageWidth, mLoadingImageHeight, ImageManager.ScalingLogic.FIT));


        new Handler().postDelayed(this, 20);

    }


    @Override
    public void onDetachedFromWindow() {
        SocketAdapter.removeFriendSocketListener(this);
        super.onDetachedFromWindow();
    }


    @Override
    public void onMatchRequest(MatchRequestSFHolder request) {

    }

    @Override
    public void onOnlineFriendStatus(OnlineFriendStatusHolder status) {

    }

    @Override
    public void onMatchResultToSender(MatchResultHolder result) {

        dismiss();
    }
}

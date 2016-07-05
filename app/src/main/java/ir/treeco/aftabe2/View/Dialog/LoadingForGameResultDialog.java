package ir.treeco.aftabe2.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import ir.treeco.aftabe2.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe2.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe2.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe2.API.Socket.SocketAdapter;
import ir.treeco.aftabe2.API.Socket.Interfaces.SocketListener;
import ir.treeco.aftabe2.Adapter.Cache.UserActionCache;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Synchronization.Synchronize;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.TimerView;
import ir.treeco.aftabe2.View.Custom.ToastMaker;
import ir.treeco.aftabe2.View.Custom.UserLevelView;
import ir.treeco.aftabe2.View.Fragment.GameResultFragment;
import ir.treeco.aftabe2.View.Fragment.OnlineGameFragment;


/**
 * Created by al on 3/16/16.
 */
public class LoadingForGameResultDialog extends Dialog implements Runnable, SocketListener {

    Context context;
    private boolean mDismissed = false;
    private ImageView mLoadingImageView;
    private static int mLoadingImageWidth = 0, mLoadingImageHeight = 0;
    private ImageManager imageManager;
    private static int[] mImageLoadingIds;
    private OnlineGameFragment.OnGameEndListener mOnGameEndListener;
    int mLoadingStep = 0;
    private User mOpponent;
    private Timer mTimer;
    TimerView mTimerView;
    int mTimerStep = 0;
    private boolean forRegister = false;
    private UserLevelView myUserLevelView;
    private UserLevelView opponentLevelView;

    public LoadingForGameResultDialog(Context context, OnlineGameFragment.OnGameEndListener onGameEndListener, User opponent, int timerStep) {
        super(context);
        this.context = context;
        mOpponent = opponent;
        this.mTimerStep = timerStep;
        mOnGameEndListener = onGameEndListener;
        imageManager = ImageManager.getInstance(context);
        ((MainActivity) context).setLoadingForGameResultDialog(this);
        initImageLoading();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runTimer();
            }
        }, 0, 1000);

    }

    public LoadingForGameResultDialog(Context context, int timerStep) {
        super(context);
        this.context = context;

        forRegister = true;

        imageManager = ImageManager.getInstance(context);
        initImageLoading();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runTimer();
            }
        }, 0, 1000);

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
        if (!forRegister) {
            SocketAdapter.addSocketListener(this);

        }
        mLoadingImageView.setKeepScreenOn(true);

        mTimerView = new TimerView(context);
        mTimerView.setDoOnlyBlue(true);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.dialog_game_result_loading_container_relative_layout);

        RelativeLayout.LayoutParams timerLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timerLP.topMargin = (int) (SizeManager.getScreenHeight() * 0.2f);
        timerLP.leftMargin = (SizeManager.getScreenWidth() - mTimerView.getRealWidth()) / 2;
        container.addView(mTimerView, timerLP);

        if (!forRegister) {

            initializeLevelViews(container);

        }


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        lp.height = SizeManager.getScreenHeight();
        getWindow().setAttributes(lp);


    }

    @Override
    public void onBackPressed() {
        ToastMaker.show(context, "باید صبر کنی", Toast.LENGTH_SHORT);
//        super.onBackPressed();
    }

    private void initializeLevelViews(RelativeLayout parent) {

        myUserLevelView = (UserLevelView) parent.findViewById(R.id.dialog_game_result_my_player);
        opponentLevelView = (UserLevelView) parent.findViewById(R.id.dialog_game_result_op);

        myUserLevelView.setVisibility(View.VISIBLE);
        opponentLevelView.setVisibility(View.VISIBLE);


        myUserLevelView.setUserNameTextSize(0.85f);
        opponentLevelView.setUserNameTextSize(0.85f);

        myUserLevelView.setUser(Tools.getCachedUser(context));
        opponentLevelView.setUser(mOpponent);


        myUserLevelView.setForOnlineGame(true);
        opponentLevelView.setForOnlineGame(true);

        myUserLevelView.setOnlineStateClear();
        opponentLevelView.setOnlineStateClear();

        for (GameActionResult gameActionResult : UserActionCache.getInstance().getMyList())
            myUserLevelView.setOnlineState(gameActionResult);


        for (GameActionResult gameActionResult : UserActionCache.getInstance().getOpponentList())
            opponentLevelView.setOnlineState(gameActionResult);

        LengthManager lengthManager = new LengthManager(context);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) myUserLevelView.getLayoutParams();
        lp.topMargin = (int) ((lengthManager.getHeaderHeight() - myUserLevelView.getRealWidth() * 1.05f) / 2);
        lp.leftMargin = (int) (lengthManager.getScreenWidth() * 0.07);

        RelativeLayout.LayoutParams lpTwo = (RelativeLayout.LayoutParams) opponentLevelView.getLayoutParams();
        lpTwo.topMargin = (int) ((lengthManager.getHeaderHeight() - opponentLevelView.getRealWidth() * 1.05f) / 2);
        lpTwo.leftMargin = (int) (0.93 * lengthManager.getScreenWidth() - opponentLevelView.getRealWidth());
        ;
    }

    private void runTimer() {
        if (mTimerStep == 0) {
            mTimer.cancel();
            dismiss();
            DialogAdapter.checkInternetConnection(context);
        }
        mTimerStep--;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mTimerView.setTimer(mTimerStep);

            }
        });
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
    public void onGotGame(GameResultHolder gameHolder) {

        dismiss();

    }

    public void doGameStart(GameResultHolder gameHolder) {


    }

    @Override
    public void onGameStart(GameStartObject gameStartObject) {


    }

    @Override
    public void onGotUserAction(final UserActionHolder actionHolder) {

        if (!actionHolder.getUserId().equals(Tools.getCachedUser(context).getId())) {

            if (opponentLevelView != null)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        opponentLevelView.setOnlineState(actionHolder.getAction());

                    }
                });

        }
    }

    @Override
    public void onFinishGame(ResultHolder resultHolder) {


        dismiss();


        GameResultFragment gameResultFragment = GameResultFragment.newInstance(resultHolder, mOpponent);
        FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, gameResultFragment, GameResultFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }


    @Override
    public void onDetachedFromWindow() {
        if (!forRegister) {
            SocketAdapter.removeSocketListener(this);

            if (mOnGameEndListener != null)
                mOnGameEndListener.onGameEnded();

            ((MainActivity) context).setLoadingForGameResultDialog(null);
        }
        super.onDetachedFromWindow();
    }
}

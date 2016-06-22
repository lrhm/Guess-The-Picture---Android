package ir.treeco.aftabe2.View.Fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import ir.treeco.aftabe2.Util.Logger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import ir.treeco.aftabe2.API.Socket.Interfaces.TimeLefTListener;
import ir.treeco.aftabe2.API.Socket.Objects.Answer.AnswerObject;
import ir.treeco.aftabe2.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.GameResult.OnlineLevel;
import ir.treeco.aftabe2.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe2.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe2.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe2.API.Socket.SocketAdapter;
import ir.treeco.aftabe2.API.Socket.Interfaces.SocketListener;
import ir.treeco.aftabe2.Adapter.Cache.UserActionCache;
import ir.treeco.aftabe2.Adapter.MediaAdapter;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.KeyboardView;
import ir.treeco.aftabe2.View.Dialog.ImageFullScreenDialog;
import ir.treeco.aftabe2.View.Dialog.LoadingForGameResultDialog;
import ir.treeco.aftabe2.View.Dialog.SkipAlertDialog;


public class OnlineGameFragment extends Fragment implements View.OnClickListener, KeyboardView.OnKeyboardEvent, SocketListener, TimeLefTListener {


    private boolean lost = false;
    private boolean endedGame = false;
    private boolean answerd = false;

    public interface OnGameEndListener {
        void onGameEnded();
    }

    private OnGameEndListener mOnGameEndListener = null;
    private static final String TAG = "OnlineGameFragment";

    private MainActivity mActivity;
    private Timer mTimer;
    private Integer mRemainingTime;
    private ImageView imageView;
    private Tools tools;
    private OnlineLevel level;
    private View view;
    private OnlineGameFragment gameFragment;
    private LengthManager lengthManager;
    private ImageManager imageManager;
    private String imagePath;
    private KeyboardView keyboardView;
    private GameResultHolder mGameResultHolder;
    private int state = 0;
    private ImageView skipButton;
    private String gameType = null;
    private static final String[] types = {"Match", "Random"};
    User opponent;
    RelativeLayout currectContainer;

    Long startAnimationTime;
    private ResultHolder mGameResult;
    private Object lock = new Object();

    AnswerObject answerObject;
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        SocketAdapter.setTimeLefTListener(this);
        SocketAdapter.addSocketListener(this);
        view = inflater.inflate(R.layout.fragment_game, container, false);
        gameFragment = this;
        state = getArguments().getInt("state");
        mActivity = (MainActivity) getActivity();

        if (state == 0) {
            mRemainingTime = 120;
            UserActionCache.getInstance().clearCache();
            mActivity.playerOne.setOnlineStateClear();
            mActivity.playerTwo.setOnlineStateClear();
            mActivity.setStarsDeactive();
            mActivity.setOriginalBackground(R.drawable.onlinecircles);

        }
        if (gameType == null) {
            boolean isMatch = getArguments().getBoolean("isMatch");
            gameType = types[1];
            if (isMatch) {
                gameType = types[0];
            }

            Answers.getInstance().logCustom(
                    new CustomEvent("Online Game")
                            .putCustomAttribute("Type", gameType));
        }

        Logger.d(TAG, new Gson().toJson(mGameResultHolder));

        level = mGameResultHolder.getLevels()[state];

        tools = new Tools(getContext());
        lengthManager = ((MainApplication) getActivity().getApplication()).getLengthManager();
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        mainActivity = (MainActivity) getActivity();
        setOnGameEndListener(mainActivity);

        ((MainActivity) getActivity()).setOnlineGame(true);
        opponent = mGameResultHolder.getOpponent();

        mActivity.setOnlineGameUser(opponent);

        String solution = level.getAnswer().replace("\\", "");

        skipButton = new ImageView(getContext());

        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fragment_game_keyboard_container);

        keyboardView = new KeyboardView(getContext(), solution);
        keyboardView.onKeyboardEvent = this;
        frameLayout.addView(keyboardView);
        setUpImagePlace();

        LengthManager lengthManager = new LengthManager(getContext());

        int topMargin = lengthManager.getLevelImageHeight() +
                (lengthManager.getLevelImageFrameHeight() - lengthManager.getLevelImageHeight()) / 2;


        SizeConverter skipbuttonConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.22f, 510, 200);
        int leftMargin = (int) ((int) SizeManager.getScreenWidth() / 2 - skipbuttonConverter.getWidth() / 2);

        skipButton.setImageBitmap(imageManager.loadImageFromResource(
                (state == 0) ? R.drawable.skipbutton : R.drawable.forfeit, skipbuttonConverter.mWidth,
                skipbuttonConverter.mHeight));
        RelativeLayout.LayoutParams skipButtonLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        skipButtonLP.leftMargin = leftMargin;
        skipButtonLP.topMargin = topMargin;


        ((RelativeLayout) view).addView(skipButton, skipButtonLP);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state == 0)
                    skip();
                else
                    forfeit();
            }
        });


        imageView = (ImageView) view.findViewById(R.id.image_game);
        imageView.setOnClickListener(this);

        imagePath = "file://" + getContext().getFilesDir().getPath() + "/online_game/" + level.getUrl();
        Logger.d(TAG, imagePath);

        Picasso.with(getActivity()).load(imagePath).fit().centerCrop().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Logger.d(TAG, "success on image load");

                mTimer = new Timer();
                ((MainActivity) getActivity()).setTimer(mRemainingTime);

                mTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (Looper.myLooper() == null)
                            Looper.prepare();
                        OnlineGameFragment.this.run();
                    }
                }, 1000, 1000);

                answerObject = new AnswerObject(level.getId());


            }

            @Override
            public void onError() {
                Logger.d(TAG, "on error image load");
            }
        });

        view.setKeepScreenOn(true);
        return view;
    }


    private void setUpImagePlace() {
        FrameLayout box = (FrameLayout) view.findViewById(R.id.box);
        tools.resizeView(box, lengthManager.getLevelImageWidth(), lengthManager.getLevelImageHeight());

        currectContainer = (RelativeLayout) view.findViewById(R.id.level_win_container);

        tools.resizeView(currectContainer, lengthManager.getLevelImageWidth(), lengthManager.getLevelImageHeight());


        ImageView lvlWinImage = (ImageView) view.findViewById(R.id.level_win_image_view);
        SizeConverter winImageConverter = SizeConverter.SizeConvertorFormHeight(lengthManager.getLevelImageHeight() * 0.666, 450, 450);
        lvlWinImage.setImageBitmap(imageManager.loadImageFromResource(R.drawable.levelwin, winImageConverter.mWidth, winImageConverter.mHeight));

        ImageView frame = (ImageView) view.findViewById(R.id.frame);
        frame.setImageBitmap(imageManager.loadImageFromResource(R.drawable.frame, lengthManager.getLevelImageFrameWidth(), lengthManager.getLevelImageFrameHeight(), ImageManager.ScalingLogic.FIT));
        tools.resizeView(frame, lengthManager.getLevelImageFrameWidth(), lengthManager.getLevelImageFrameHeight());


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.image_game:
                new ImageFullScreenDialog(getContext(), imagePath).show();
                break;
        }
    }


    @Override
    public void onDestroy() {


        if (state == 1 || mRemainingTime != null && mRemainingTime == 0 || lost || endedGame) {
            Logger.d(TAG, "onDestroy , set online game false");
            checkParentActivityNull();
            (mActivity).setOnlineGame(false);
            MediaAdapter.getInstance(getActivity()).pauseBomb();

            synchronized (lock) {
                if (mGameResult == null) {
                    super.onDestroy();
                    new LoadingForGameResultDialog(mActivity, mOnGameEndListener, opponent, mRemainingTime + 10).show();
                } else {


                    super.onDestroy();
                    mOnGameEndListener.onGameEnded();

//                    mainActivity.setOnlineGameVisibilityGone();
                    GameResultFragment gameResultFragment = GameResultFragment.newInstance(mGameResult, opponent);
                    FragmentTransaction transaction = (mActivity).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, gameResultFragment, GameResultFragment.TAG);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }

            }
        } else {

            super.onDestroy();
        }


    }


    @Override
    public void onHintClicked() {

    }


    private void startShowingAnimation() {

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(1);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                startAnimationTime = System.currentTimeMillis();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Logger.d(TAG, "Animation End");

                checkParentActivityNull();
                mActivity.getSupportFragmentManager().popBackStack();


                if (state == 1)
                    return;


                Bundle bundle = new Bundle();
                bundle.putInt("state", 1);

                OnlineGameFragment gameFragment = new OnlineGameFragment();
                gameFragment.mRemainingTime = mRemainingTime;
                gameFragment.gameType = gameType;
                gameFragment.setOnGameEndListener(mOnGameEndListener);
                gameFragment.setGameResultHolder(mGameResultHolder);
                gameFragment.setArguments(bundle);

                FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, gameFragment, "FRAGMENT_ONLINE_GAME");
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        currectContainer.startAnimation(alphaAnimation);
        currectContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAllAnswered(String guess) {

        String solution = level.getAnswer().replace("\\", "");

        if ((guess.replace("آ", "ا")).equals((solution.replace("/",
                "")).replace("آ", "ا"))) {


            MediaAdapter.getInstance(getContext()).playCorrectSound();

            answerd = true;


            mTimer.cancel();


            GameActionResult gameActionResult = new GameActionResult("correct");

            UserActionCache.getInstance().addToMyList(gameActionResult);

            mainActivity.playerOne.setOnlineState(gameActionResult);

            answerObject.setCorrect();
            SocketAdapter.setAnswerLevel(answerObject);

            startShowingAnimation();


        }

    }

    public void setGameResultHolder(GameResultHolder GameResultHolder) {
        this.mGameResultHolder = GameResultHolder;
    }

    public void doSkip() {


        if (!checkParentActivityNull())
            return;

        mActivity.getSupportFragmentManager().popBackStack();

        answerObject.setSkip();
        SocketAdapter.setAnswerLevel(answerObject);


        GameActionResult gameActionResult = new GameActionResult("skip");

        UserActionCache.getInstance().addToMyList(gameActionResult);

        mainActivity.playerOne.setOnlineState(gameActionResult);

        Bundle bundle = new Bundle();
        bundle.putInt("state", 1);

        OnlineGameFragment gameFragment = new OnlineGameFragment();
        gameFragment.mRemainingTime = mRemainingTime;
        gameFragment.mGameResult = mGameResult;
        gameFragment.gameType = gameType;
        gameFragment.setOnGameEndListener(mOnGameEndListener);
        gameFragment.setGameResultHolder(mGameResultHolder);
        gameFragment.setArguments(bundle);

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, gameFragment, "FRAGMENT_ONLINE_GAME");
        transaction.addToBackStack(null);

        transaction.commitAllowingStateLoss();
    }

    public void skip() {
        if (state == 1)
            return;

        new SkipAlertDialog(getContext(), "مطمئنی می خوای رد شی؟" + "\n" + "دیگه نمیتونی برگردی!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSkip();
            }
        }, null).show();


    }

    public void forfeit() {

        new SkipAlertDialog(getContext(), "میبازی ها !", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkParentActivityNull()) {
                    mActivity.getSupportFragmentManager().popBackStack();
                    doLose();

                }
            }
        }, null).show();


    }

    public void doLose() {
        if (answerObject == null) {
            if (mGameResultHolder == null)
                return;
            answerObject = new AnswerObject(mGameResultHolder.getLevels()[state].getId());

        }
        answerObject.setSkip();
        SocketAdapter.setAnswerLevel(answerObject);

        GameActionResult gameActionResult = new GameActionResult("skip");
        UserActionCache.getInstance().addToMyList(gameActionResult);

        if (state == 0) {
            AnswerObject answerObject1 = new AnswerObject(mGameResultHolder.getLevels()[1].getId());
            answerObject1.setSkip();
            UserActionCache.getInstance().addToMyList(gameActionResult);

            SocketAdapter.setAnswerLevel(answerObject1);
        }


        lost = true;


    }

    public boolean checkParentActivityNull() {
        if (mActivity == null && getActivity() != null)
            mActivity = (MainActivity) getActivity();
        return mActivity != null;

    }

    public void run() {

        if (mRemainingTime == 0) {

            Logger.d(TAG, "state is " + state);
            mTimer.cancel();
            final GameActionResult gameActionResult = new GameActionResult("skip");
            UserActionCache.getInstance().addToMyList(gameActionResult);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.playerOne.setOnlineState(gameActionResult);
                    answerObject.setSkip();
//                    SocketAdapter.setAnswerLevel(answerObject);

                    checkParentActivityNull();
                    if (mActivity != null)
                        if (!mActivity.isPaused())
                            mActivity.getSupportFragmentManager().popBackStack();

                    if (state == 1) {
                        Logger.d(TAG, "return mikonim dg ");
                        return;
                    } else {

                        AnswerObject answerObject1 = new AnswerObject(mGameResultHolder.getLevels()[1].getId());
                        answerObject1.setSkip();

//                        SocketAdapter.setAnswerLevel(answerObject1);
                        UserActionCache.getInstance().addToMyList(gameActionResult);


                        return;

                    }

                }
            });

            return;


        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (checkParentActivityNull()) {
                    (mActivity).setTimer(mRemainingTime);

                }
            }
        });
        mRemainingTime--;

        if (checkParentActivityNull())
            if (mRemainingTime < 28 && !MediaAdapter.getInstance(getActivity()).isBombPlaying()) {
                MediaAdapter.getInstance(getActivity()).playBomb();
            }


    }

    @Override
    public void onDestroyView() {

        Logger.d(TAG, "on destroy");
        SocketAdapter.removeSocketListener(this);
        if (mTimer != null)
            mTimer.cancel();
        super.onDestroyView();
    }

    @Override
    public void onGotGame(GameResultHolder gameHolder) {

    }

    @Override
    public void onGameStart(GameStartObject gameStartObject) {


    }

    @Override
    public void onGotUserAction(final UserActionHolder actionHolder) {


    }

    @Override
    public void onFinishGame(ResultHolder resultHolder) {

        synchronized (lock) {
            endedGame = true;
            mGameResult = resultHolder;
        }

        long delay = 0;
        if (resultHolder.amIWinner(Tools.getCachedUser(getContext()))) {
            long current = System.currentTimeMillis();
            if (startAnimationTime != null && current - startAnimationTime < 2000) {
                delay = 2000 - (current - startAnimationTime);
            }
        }


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!mGameResult.amIWinner(Tools.getCachedUser(getContext())) && !answerd)
                    doLose();
                endedGame = true;
                if (checkParentActivityNull())
                    if (!mainActivity.isPaused())
                        mainActivity.getSupportFragmentManager().popBackStack();

            }
        }, delay);

    }


    @Override
    public void onResume() {
        super.onResume();


        if (mRemainingTime <= 0 || lost || endedGame) {

            checkParentActivityNull();
            mainActivity.getSupportFragmentManager().popBackStack();

        }
    }

    @Override
    public void onTime(int milies) {
        mRemainingTime = milies / 1000;
    }


    public void setOnGameEndListener(OnGameEndListener onGameEndListener) {
        this.mOnGameEndListener = onGameEndListener;
    }
}

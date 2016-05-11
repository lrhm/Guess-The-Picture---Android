package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import ir.treeco.aftabe.API.Socket.Objects.Answer.AnswerObject;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.OnlineLevel;
import ir.treeco.aftabe.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.API.Socket.SocketListener;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.KeyboardView;
import ir.treeco.aftabe.View.Dialog.ImageFullScreenDialog;
import ir.treeco.aftabe.View.Dialog.LoadingForGameResultDialog;
import ir.treeco.aftabe.View.Dialog.CustomAlertDialog;


public class OnlineGameFragment extends Fragment implements View.OnClickListener, KeyboardView.OnKeyboardEvent, SocketListener {


    private boolean lost = false;

    public interface OnGameEndListener {
        void onGameEnded();
    }

    private OnGameEndListener mOnGameEndListener = null;
    private static final String TAG = "OnlineGameFragment";

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
    User opponent;

    private ResultHolder mGameResult;
    private Object lock = new Object();

    AnswerObject answerObject;
    MainActivity mainActivity;
    String baseUrl = "https://aftabe2.com:2020/api/pictures/level/download/";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (mRemainingTime == null)
            mRemainingTime = 120;
        SocketAdapter.addSocketListener(this);
        view = inflater.inflate(R.layout.fragment_game, container, false);
        gameFragment = this;
        state = getArguments().getInt("state");

        Log.d(TAG, new Gson().toJson(mGameResultHolder));

        level = mGameResultHolder.getLevels()[state];

        tools = new Tools(getContext());
        lengthManager = ((MainApplication) getActivity().getApplication()).getLengthManager();
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        mainActivity = (MainActivity) getActivity();
        setOnGameEndListener(mainActivity);

        ((MainActivity) getActivity()).setOnlineGame(true);
        opponent = new User();
        opponent.setName(mGameResultHolder.getOpponent().getName());
        opponent.setId(mGameResultHolder.getOpponent().getId());
        opponent.setScore(mGameResultHolder.getOpponent().getScore());
        opponent.setAccess(mGameResultHolder.getOpponent().getAccess());

        ((MainActivity) getActivity()).setOnlineGameUser(opponent);

        String solution = level.getAnswer();

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
        Log.d(TAG, imagePath);

        Picasso.with(getActivity()).load(imagePath).fit().centerCrop().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "success on image load");

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
                Log.d(TAG, "on error image load");
            }
        });

        view.setKeepScreenOn(true);
        return view;
    }


    private void setUpImagePlace() {
        FrameLayout box = (FrameLayout) view.findViewById(R.id.box);
        tools.resizeView(box, lengthManager.getLevelImageWidth(), lengthManager.getLevelImageHeight());

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


        if (state == 1 || state == 0 && mRemainingTime == 0 || lost) {
            Log.d(TAG, "onDestroy , set online game false");
            ((MainActivity) getActivity()).setOnlineGame(false);

            synchronized (lock) {
                if (mGameResult == null) {
                    super.onDestroy();
                    new LoadingForGameResultDialog(getActivity(), mOnGameEndListener, opponent, mRemainingTime).show();
                } else {
                    boolean win = false;
                    if (mGameResult.getScores()[0].getUserId().equals(Tools.getCachedUser().getId()))
                        win = mGameResult.getScores()[0].isWinner();

                    if (mGameResult.getScores()[1].getUserId().equals(Tools.getCachedUser().getId()))
                        win = mGameResult.getScores()[1].isWinner();


                    super.onDestroy();
                    mOnGameEndListener.onGameEnded();

//                    mainActivity.setOnlineGameVisibilityGone();
                    GameResultFragment gameResultFragment = GameResultFragment.newInstance(win, mGameResult, opponent);
                    FragmentTransaction transaction = (getActivity()).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, gameResultFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            }
        } else {

            super.onDestroy();
        }


    }


    @Override
    public void onHintClicked() {

    }

    @Override
    public void onAllAnswered(String guess) {

        String solution = level.getAnswer();

        if ((guess.replace("آ", "ا")).equals((solution.replace("/",
                "")).replace("آ", "ا"))) {


            getActivity().getSupportFragmentManager().popBackStack();


            mTimer.cancel();

            Toast.makeText(getContext(), "answer is right", Toast.LENGTH_LONG);

            GameActionResult gameActionResult = new GameActionResult("correct");
            mainActivity.playerOne.setOnlineState(gameActionResult);

            answerObject.setCorrect();
            SocketAdapter.setAnswerLevel(answerObject);

            if (state == 1) {

                getActivity().getSupportFragmentManager().popBackStack();


                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("state", 1);

            OnlineGameFragment gameFragment = new OnlineGameFragment();
            gameFragment.mRemainingTime = mRemainingTime;
            gameFragment.setOnGameEndListener(mOnGameEndListener);
            gameFragment.setGameResultHolder(mGameResultHolder);
            gameFragment.setArguments(bundle);

            FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, gameFragment, "FRAGMENT_ONLINE_GAME");
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    public void setGameResultHolder(GameResultHolder GameResultHolder) {
        this.mGameResultHolder = GameResultHolder;
    }

    public void doSkip() {

        getActivity().getSupportFragmentManager().popBackStack();

        answerObject.setSkip();
        SocketAdapter.setAnswerLevel(answerObject);


        GameActionResult gameActionResult = new GameActionResult("skip");
        mainActivity.playerOne.setOnlineState(gameActionResult);

        Bundle bundle = new Bundle();
        bundle.putInt("state", 1);

        OnlineGameFragment gameFragment = new OnlineGameFragment();
        gameFragment.mRemainingTime = mRemainingTime;
        gameFragment.mGameResult = mGameResult;
        gameFragment.setOnGameEndListener(mOnGameEndListener);
        gameFragment.setGameResultHolder(mGameResultHolder);
        gameFragment.setArguments(bundle);

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, gameFragment, "FRAGMENT_ONLINE_GAME");
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void skip() {
        if (state == 1)
            return;

        new CustomAlertDialog(getContext(), "مطمپنی میخوای رد شی ؟" + "\n" + "دیگه نمیتونی برگردی!", "باشه", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSkip();
            }
        }, "نه", null).show();


    }

    public void forfeit() {

        new CustomAlertDialog(getContext(), "میبازی ها !", "باشه", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLose();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }, "ادامه میدم", null).show();


    }

    public void doLose() {

        answerObject.setSkip();
        SocketAdapter.setAnswerLevel(answerObject);

        if (state == 0) {
            AnswerObject answerObject1 = new AnswerObject(mGameResultHolder.getLevels()[1].getId());
            answerObject1.setSkip();

            SocketAdapter.setAnswerLevel(answerObject1);
        }
        lost = true;


    }

    public void run() {

        if (mRemainingTime == 0) {

            Log.d(TAG, "state is " + state);
            mTimer.cancel();
            final GameActionResult gameActionResult = new GameActionResult("skip");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.playerOne.setOnlineState(gameActionResult);
                    answerObject.setSkip();
                    SocketAdapter.setAnswerLevel(answerObject);

                    if (!((MainActivity) getActivity()).isPaused())
                        getActivity().getSupportFragmentManager().popBackStack();

                    if (state == 1) {
                        Log.d(TAG, "return mikonim dg ");
                        return;
                    } else {

                        AnswerObject answerObject1 = new AnswerObject(mGameResultHolder.getLevels()[1].getId());
                        answerObject1.setSkip();

                        SocketAdapter.setAnswerLevel(answerObject1);


                        return;

                    }

                }
            });

            return;


        }
        mRemainingTime--;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null)
                    return;
                ((MainActivity) getActivity()).setTimer(mRemainingTime);

            }
        });


    }

    @Override
    public void onDestroyView() {

        Log.d(TAG, "on destroy");
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
            mGameResult = resultHolder;
        }
        doLose();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mainActivity.getSupportFragmentManager().popBackStack();

            }
        });

    }


    public void setOnGameEndListener(OnGameEndListener onGameEndListener) {
        this.mOnGameEndListener = onGameEndListener;
    }
}

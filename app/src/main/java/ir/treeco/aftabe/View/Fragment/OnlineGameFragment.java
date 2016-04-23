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
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.Socket.Objects.Answer.AnswerObject;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.OnlineLevel;
import ir.treeco.aftabe.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.API.Socket.SocketListener;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.Adapter.CoinAdapter;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.KeyboardAdapter;
import ir.treeco.aftabe.Adapter.SolutionAdapter;
import ir.treeco.aftabe.Interface.FinishLevel;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.CheatDrawable;
import ir.treeco.aftabe.View.Custom.KeyboardView;
import ir.treeco.aftabe.View.Dialog.FinishDailog;
import ir.treeco.aftabe.View.Dialog.ImageFullScreenDialog;


public class OnlineGameFragment extends Fragment implements View.OnClickListener, KeyboardView.OnKeyboardEvent, SocketListener {

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

        level = mGameResultHolder.getLevels()[state];

        tools = new Tools(getContext());
        lengthManager = ((MainApplication) getActivity().getApplication()).getLengthManager();
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        mainActivity = (MainActivity) getActivity();

        ((MainActivity) getActivity()).setOnlineGame(true);
        User opponent = new User();
        opponent.setName(mGameResultHolder.getOpponent().getName());
        opponent.setId(mGameResultHolder.getOpponent().getId());
        opponent.setScore(mGameResultHolder.getOpponent().getScore());

        ((MainActivity) getActivity()).setOnlineGameUser(opponent);

        String solution = level.getAnswer();

        skipButton = new ImageView(getContext());

        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fragment_game_keyboard_container);

        keyboardView = new KeyboardView(getContext(), solution);
        keyboardView.onKeyboardEvent = this;
        frameLayout.addView(keyboardView);
        setUpImagePlace();


        SizeConverter skipbuttonConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.17f, 510, 200);
        int leftMargin = (int) ((int) SizeManager.getScreenWidth() * 0.5f - skipbuttonConverter.getWidth() / 2);

        skipButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.skipbutton, skipbuttonConverter.mWidth,
                skipbuttonConverter.mHeight));
        FrameLayout.LayoutParams skipButtonLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        skipButtonLP.leftMargin = leftMargin;


        if (state == 0)
            frameLayout.addView(skipButton, skipButtonLP);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                skip();
            }
        });


        imageView = (ImageView) view.findViewById(R.id.image_game);
        imageView.setOnClickListener(this);

        imagePath = baseUrl + level.getUrl();
        Log.d(TAG, imagePath);

        Picasso.with(getActivity()).load(imagePath).fit().centerCrop().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "success on image load");

                if (state == 0) {
                    SocketAdapter.setReadyStatus();
                    imageView.setVisibility(View.INVISIBLE);

                }
                if (state == 1) {
                    imageView.setVisibility(View.VISIBLE);
                    answerObject = new AnswerObject(level.getId());
                    mTimer = new Timer();
                    mTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (Looper.myLooper() == null)
                                Looper.prepare();
                            OnlineGameFragment.this.run();
                        }
                    }, 1000, 1000);
                }


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
        frame.setImageBitmap(imageManager.loadImageFromResource(R.drawable.frame, lengthManager.getLevelImageFrameWidth(), lengthManager.getLevelImageFrameHeight()));
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
        super.onDestroy();
        ((MainActivity) getActivity()).setOnlineGame(false);


    }


    @Override
    public void onHintClicked() {

    }

    @Override
    public void onAllAnswered(String guess) {

        String solution = level.getAnswer();

        if ((guess.replace("آ", "ا")).equals((solution.replace("/",
                "")).replace("آ", "ا"))) {


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
            gameFragment.setGameResultHolder(mGameResultHolder);
            gameFragment.setArguments(bundle);

            FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, gameFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    public void setGameResultHolder(GameResultHolder GameResultHolder) {
        this.mGameResultHolder = GameResultHolder;
    }

    public void skip() {
        if (state == 1)
            return;


        getActivity().getSupportFragmentManager().popBackStack();

        answerObject.setSkip();
        SocketAdapter.setAnswerLevel(answerObject);


        GameActionResult gameActionResult = new GameActionResult("skip");
        mainActivity.playerOne.setOnlineState(gameActionResult);

        Bundle bundle = new Bundle();
        bundle.putInt("state", 1);

        OnlineGameFragment gameFragment = new OnlineGameFragment();
        gameFragment.mRemainingTime = mRemainingTime;
        gameFragment.setGameResultHolder(mGameResultHolder);
        gameFragment.setArguments(bundle);

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, gameFragment);
        transaction.addToBackStack(null);

        transaction.commit();


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

                    if (state == 1) {

                        Log.d(TAG, "return mikonim dg ");
                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().getSupportFragmentManager().popBackStack();

                        return;
                    } else {

                        AnswerObject answerObject1 = new AnswerObject(mGameResultHolder.getLevels()[1].getId());
                        answerObject1.setSkip();

                        SocketAdapter.setAnswerLevel(answerObject1);

                        getActivity().getSupportFragmentManager().popBackStack();

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
                ((MainActivity) getActivity()).setTimer(mRemainingTime);

            }
        });


    }

    @Override
    public void onDestroyView() {

        Log.d(TAG, "on destroy");
        SocketAdapter.removeSocketListener(this);
        mTimer.cancel();
        super.onDestroyView();
    }

    @Override
    public void onGotGame(GameResultHolder gameHolder) {

    }

    @Override
    public void onGameStart(GameStartObject gameStartObject) {
        if (Looper.myLooper() == null)
            Looper.prepare();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(View.VISIBLE);
            }
        });
        answerObject = new AnswerObject(level.getId());
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Looper.myLooper() == null)
                    Looper.prepare();
                OnlineGameFragment.this.run();
            }
        }, 1000, 1000);
    }

    @Override
    public void onGotUserAction(final UserActionHolder actionHolder) {


    }

    @Override
    public void onFinishGame(ResultHolder resultHolder) {

    }
}

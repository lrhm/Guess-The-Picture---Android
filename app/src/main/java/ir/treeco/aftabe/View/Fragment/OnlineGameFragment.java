package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.Socket.Objects.Answer.AnswerObject;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.OnlineLevel;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
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
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.CheatDrawable;
import ir.treeco.aftabe.View.Custom.KeyboardView;
import ir.treeco.aftabe.View.Dialog.FinishDailog;
import ir.treeco.aftabe.View.Dialog.ImageFullScreenDialog;


public class OnlineGameFragment extends Fragment implements View.OnClickListener, KeyboardView.OnKeyboardEvent {

    private static final String TAG = "OnlineGameFragment";

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
    AnswerObject answerObject;
    MainActivity mainActivity;
    String baseUrl = "https://aftabe2.com:2020/api/pictures/level/download/";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_game, container, false);
        gameFragment = this;
        state = getArguments().getInt("state");

        level = mGameResultHolder.getLevels()[state];
        answerObject = new AnswerObject(level.getId());

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

        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fragment_game_keyboard_container);

        keyboardView = new KeyboardView(getContext(), solution);
        keyboardView.onKeyboardEvent = this;
        frameLayout.addView(keyboardView);
        setUpImagePlace();

        imageView = (ImageView) view.findViewById(R.id.image_game);
        imageView.setOnClickListener(this);

        imagePath = baseUrl + level.getUrl();
        Log.d(TAG, imagePath);

        Picasso.with(getActivity()).load(imagePath).into(imageView);
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

            Toast.makeText(getContext(), "answer is right nigga", Toast.LENGTH_LONG);

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


}

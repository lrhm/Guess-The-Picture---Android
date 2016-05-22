package ir.treeco.aftabe.View.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import ir.treeco.aftabe.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe.Adapter.Cache.UserActionCache;
import ir.treeco.aftabe.Adapter.MediaAdapter;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.UserLevelView;
import ir.treeco.aftabe.View.Dialog.SkipAlertDialog;


public class GameResultFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM2 = "param_game_result_holder";
    private static final String ARG_USER_OP = "param_user_oppoenent";
    private static final String TAG = "GameResultFragment";

    private ResultHolder mGameResultHolder;
    private User mOpponent;

    private boolean mWin;
    private boolean mDraw;

    ImageView mAddFriendImageView;
    ImageView mBackImageView;


    public GameResultFragment() {
        // Required empty public constructor
    }

    public static GameResultFragment newInstance(ResultHolder gameResultHolder, User opponent) {
        GameResultFragment fragment = new GameResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, new Gson().toJson(gameResultHolder));
        args.putString(ARG_USER_OP, new Gson().toJson(opponent));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGameResultHolder = new Gson().fromJson(getArguments().getString(ARG_PARAM2), ResultHolder.class);
            mOpponent = new Gson().fromJson(getArguments().getString(ARG_USER_OP), User.class);

            mWin = false;
            if (mGameResultHolder.getScores()[0].getUserId().equals(Tools.getCachedUser(getActivity()).getId()))
                mWin = mGameResultHolder.getScores()[0].isWinner();

            if (mGameResultHolder.getScores()[1].getUserId().equals(Tools.getCachedUser(getActivity()).getId()))
                mWin = mGameResultHolder.getScores()[1].isWinner();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.d("TAG", "win is " + mWin);

        mDraw = mGameResultHolder.getScores()[0].isWinner() && mGameResultHolder.getScores()[1].isWinner();

        ((MainActivity) getActivity()).setGameResult(true);

        View view = inflater.inflate(R.layout.fragment_game_result, container, false);
        User myUser = Tools.getCachedUser(getActivity());

        TextView coinTextView = (TextView) view.findViewById(R.id.fragment_result_coin_tv);
        TextView scoreTextView = (TextView) view.findViewById(R.id.fragment_result_score_tv);

        View firstShapeContainer = view.findViewById(R.id.fragment_result_shape_container);
        View secondShapeContainer = view.findViewById(R.id.fragment_result_shape_container_below);
        View secShapeContainerTmp = view.findViewById(R.id.fragment_result_shape_container_below_tmp);

        UserLevelView myUserLevelView = (UserLevelView) view.findViewById(R.id.fragment_result_my_user_level_view);
        UserLevelView opponentLevelView = (UserLevelView) view.findViewById(R.id.fragment_result_op_user_level_view);


        opponentLevelView.setForOnlineGame(false);
        opponentLevelView.setUser(mOpponent);
        opponentLevelView.setOnlineStateClear();

        for (GameActionResult gameActionResult : UserActionCache.getInstance().getOpponentList()) {
            opponentLevelView.setOnlineState(gameActionResult);
            Log.d(TAG, "Enemys one");
        }

        mAddFriendImageView = (ImageView) view.findViewById(R.id.fragment_result_add_friend);
        mBackImageView = (ImageView) view.findViewById(R.id.fragment_result_chat);

        ImageView resultImageView = (ImageView) view.findViewById(R.id.fragment_result_win_or_lose_iv);
        initResultImageView(resultImageView);

        initShapeLP(firstShapeContainer.getLayoutParams());
        initShapeLP(secondShapeContainer.getLayoutParams());
        initShapeLP(secShapeContainerTmp.getLayoutParams());

        myUserLevelView.setForOnlineGame(false);

        myUserLevelView.setUser(myUser);
        myUserLevelView.setOnlineStateClear();

        for (GameActionResult gameActionResult : UserActionCache.getInstance().getMyList()) {
            myUserLevelView.setOnlineState(gameActionResult);
            Log.d(TAG, "my one");
        }

        int coin = 0;
        if (mWin)
            coin = 160;
        if (mDraw)
            coin = 80;

        if (!mWin) {
            MediaAdapter.getInstance(getContext()).playLoseSound();
        }

        String winText = Tools.numeralStringToPersianDigits(coin + "") + " " + "سکه" + " ";
        coinTextView.setTypeface(FontsHolder.getNumeralSansBold(getContext()));
        coinTextView.setText(winText);

        String scoreText = Tools.numeralStringToPersianDigits(mGameResultHolder.getMyScoreResult(myUser) + "") + " امتیاز"; // ;
        scoreTextView.setText(scoreText);
        scoreTextView.setTypeface(FontsHolder.getNumeralSansBold(getContext()));

        UiUtil.setTextViewSize(scoreTextView, (int) (SizeManager.getScreenHeight() * 0.075), 0.4f);
        UiUtil.setTextViewSize(coinTextView, (int) (SizeManager.getScreenHeight() * 0.075), 0.4f);
        UiUtil.setLeftMargin(scoreTextView, (int) (SizeManager.getScreenWidth() * 0.13));
        UiUtil.setRightMargin(coinTextView, (int) (SizeManager.getScreenWidth() * 0.13));


        UiUtil.setTopMargin(coinTextView, (int) (0.04 * SizeManager.getScreenWidth()));
        UiUtil.setTopMargin(scoreTextView, (int) (0.04 * SizeManager.getScreenWidth()));
        int shapeHeight = (int) (SizeManager.getScreenHeight() * 0.075);
        UiUtil.setHeight(coinTextView, shapeHeight);
        UiUtil.setHeight(scoreTextView, shapeHeight);


        ((MainActivity) getActivity()).setStarts(mGameResultHolder.getMyScoreResult(myUser));

        ImageManager imageManager = new ImageManager(getContext());

        int width = (int) (SizeManager.getScreenWidth() * 0.16);


        mAddFriendImageView.setImageBitmap(imageManager.loadImageFromResource(
                (mOpponent.isFriend()) ? R.drawable.chatbutton : R.drawable.addfriends, width, width));
        mAddFriendImageView.setOnClickListener(this);
        mBackImageView.setOnClickListener(this);
        mBackImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.continuebutton, width, width));

        ((RelativeLayout.LayoutParams) mAddFriendImageView.getLayoutParams()).topMargin = (int) (0.035 * SizeManager.getScreenWidth());
        ((RelativeLayout.LayoutParams) mBackImageView.getLayoutParams()).topMargin = (int) (0.035 * SizeManager.getScreenWidth());
        int leftMargin = (int) (SizeManager.getScreenWidth() * 0.1 - width * 0.1);
        UiUtil.setLeftMargin(mAddFriendImageView, leftMargin);
        leftMargin = (int) (SizeManager.getScreenWidth() * 0.9 - width * 0.9);
        UiUtil.setLeftMargin(mBackImageView, leftMargin);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) secShapeContainerTmp.getLayoutParams();
        layoutParams.leftMargin = (int) (SizeManager.getScreenWidth() * 0.1);

        return view;

    }

    private void initResultImageView(ImageView resultImageView) {

        int imgId = (mWin) ? R.drawable.aftabewin : R.drawable.aftabelose;
        if (mDraw)
            imgId = R.drawable.aftabedraw;

        int width = (int) (SizeManager.getScreenWidth() * 0.65);
        ((LinearLayout.LayoutParams) resultImageView.getLayoutParams()).leftMargin =
                +(int) (SizeManager.getScreenWidth() * 0.17);
        ImageManager imageManager = new ImageManager(getContext());
        resultImageView.setImageBitmap(imageManager.loadImageFromResource(imgId, width, width));

    }

    private void initShapeLP(ViewGroup.LayoutParams lp) {
        lp.width = (int) (SizeManager.getScreenWidth() * 0.8);
        lp.height = (int) (SizeManager.getScreenHeight() * 0.075);

        if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) lp).topMargin = (int) (0.04 * SizeManager.getScreenWidth());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).setGameResult(false);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fragment_result_add_friend) {
            if (!mOpponent.isFriend()) {
                new SkipAlertDialog(getActivity(), "درخواست دوستی", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mOpponent.isBot())
                            AftabeAPIAdapter.requestFriend(Tools.getCachedUser(getActivity()), mOpponent.getId(), null);
                    }
                }, null);
            }
        }
        if (v.getId() == R.id.fragment_result_chat) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}

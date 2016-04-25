package ir.treeco.aftabe.View.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.UserLevelView;


public class GameResultFragment extends Fragment {

    private static final String ARG_WIN_OR_LOSE = "param_win_or_lose";
    private static final String ARG_PARAM2 = "param2";

    private boolean mWin;

    ImageView mAddFriendImageView;
    ImageView mChatImageView;


    public GameResultFragment() {
        // Required empty public constructor
    }

    public static GameResultFragment newInstance(Boolean win) {
        GameResultFragment fragment = new GameResultFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM2, win);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWin = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setHeaderVisiblity(false);

        View view = inflater.inflate(R.layout.fragment_game_result, container, false);
        User myUser = Tools.getCachedUser();

        TextView coinTextView = (TextView) view.findViewById(R.id.fragment_result_coin_tv);
        TextView scoreTextView = (TextView) view.findViewById(R.id.fragment_result_score_tv);

        View firstShapeContainer = view.findViewById(R.id.fragment_result_shape_container);
        View secondShapeContainer = view.findViewById(R.id.fragment_result_shape_container_below);
        View secShapeContainerTmp = view.findViewById(R.id.fragment_result_shape_container_below_tmp);

        UserLevelView myUserLevelView = (UserLevelView) view.findViewById(R.id.fragment_result_my_user_level_view);
        UserLevelView opponentLevelView = (UserLevelView) view.findViewById(R.id.fragment_result_op_user_level_view);

        mAddFriendImageView = (ImageView) view.findViewById(R.id.fragment_result_add_friend);
        mChatImageView = (ImageView) view.findViewById(R.id.fragment_result_chat);

        ImageView resultImageView = (ImageView) view.findViewById(R.id.fragment_result_win_or_lose_iv);
        initResultImageView(resultImageView);

        initShapeLP(firstShapeContainer.getLayoutParams());
        initShapeLP(secondShapeContainer.getLayoutParams());
        initShapeLP(secShapeContainerTmp.getLayoutParams());



        myUserLevelView.setUser(myUser);

        coinTextView.setText("coin");
        scoreTextView.setText("score");

        ImageManager imageManager = new ImageManager(getContext());

        int width = (int) (SizeManager.getScreenWidth() * 0.14);


        mAddFriendImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.addfriends, width, width));
        mChatImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.chatbutton, width, width));

        ((RelativeLayout.LayoutParams) mAddFriendImageView.getLayoutParams()).topMargin = (int) (0.04 * SizeManager.getScreenWidth());
        ((RelativeLayout.LayoutParams) mChatImageView.getLayoutParams()).topMargin = (int) (0.04 * SizeManager.getScreenWidth());

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) secShapeContainerTmp.getLayoutParams();
        layoutParams.leftMargin = (int) (SizeManager.getScreenWidth() * 0.1);

        return view;

    }

    private void initResultImageView(ImageView resultImageView) {
        int imgId = (mWin) ? R.drawable.aftabewin : R.drawable.aftabelose;
        int width = (int) (SizeManager.getScreenWidth() * 0.7);

        ImageManager imageManager = new ImageManager(getContext());
        resultImageView.setImageBitmap(imageManager.loadImageFromResource(imgId, width, width));

    }

    private void initShapeLP(ViewGroup.LayoutParams lp) {
        lp.width = (int) (SizeManager.getScreenWidth() * 0.8);
        lp.height = (int) (SizeManager.getScreenHeight() * 0.075);

        if(lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) lp).topMargin = (int) (0.04 * SizeManager.getScreenWidth());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).setHeaderVisiblity(true);

    }
}

package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import ir.treeco.aftabe.Adapter.CoinAdapter;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.KeyboardAdapter;
import ir.treeco.aftabe.Adapter.SolutionAdapter;
import ir.treeco.aftabe.Interface.FinishLevel;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.CheatDrawable;
import ir.treeco.aftabe.View.Custom.KeyboardView;
import ir.treeco.aftabe.View.Dialog.FinishDailog;
import ir.treeco.aftabe.View.Dialog.ImageFullScreenDialog;

public class OnlineGameFragment extends Fragment implements View.OnClickListener , KeyboardView.OnKeyboardEvent {
    private int levelId;
    private ImageView imageView;
    private int packageId;
    private Tools tools;
    private DBAdapter db;
    private Level level;
    private View view;
    private View[] cheatButtons;
    private View blackWidow;
    private String solution;
    private int packageSize;
    private OnlineGameFragment gameFragment;
    private CoinAdapter coinAdapter;
    private LengthManager lengthManager;
    private ImageManager imageManager;
    private String imagePath;
    private KeyboardView keyboardView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_game, container, false);
        gameFragment = this;

        tools = new Tools(getContext());
        db = DBAdapter.getInstance(getActivity());
        coinAdapter = new CoinAdapter(getActivity(), getActivity());
        lengthManager = ((MainApplication) getActivity().getApplication()).getLengthManager();
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        ((MainActivity)getActivity()).setOnlineGame(true);

        levelId = getArguments().getInt("LevelId");
        packageId = getArguments().getInt("id");

        level = db.getLevel(packageId, levelId);
        packageSize = db.getLevels(packageId).length;

        solution = tools.decodeBase64(level.getJavab());
        StringBuilder stringBuilder = new StringBuilder(solution);

        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fragment_game_keyboard_container);

        keyboardView = new KeyboardView(getContext(), solution);
        keyboardView.onKeyboardEvent = this;
        frameLayout.addView(keyboardView);
        setUpImagePlace();

        imageView = (ImageView) view.findViewById(R.id.image_game);
        imageView.setOnClickListener(this);

        imagePath = "file://" + getActivity().getFilesDir().getPath() + "/Downloaded/"
                + packageId + "_" + level.getResources();

        Picasso.with(getActivity()).load(imagePath).into(imageView);
        return view;
    }




    private void setUpImagePlace() {
        FrameLayout box = (FrameLayout) view.findViewById(R.id.box);
        tools.resizeView(box, lengthManager.getLevelImageWidth(), lengthManager.getLevelImageHeight());

        ImageView frame = (ImageView) view.findViewById(R.id.frame);
        frame.setImageBitmap(imageManager.loadImageFromResource(R.drawable.frame, lengthManager.getLevelImageFrameWidth(), lengthManager.getLevelImageFrameHeight()));
        tools.resizeView(frame, lengthManager.getLevelImageFrameWidth(), lengthManager.getLevelImageFrameHeight());


        blackWidow = view.findViewById(R.id.black_widow);
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
        ((MainActivity)getActivity()).setOnlineGame(false);


    }


    @Override
    public void onHintClicked() {

    }

    @Override
    public void onAllAnswered(String guess) {

        if ((guess.replace("آ", "ا")).equals((solution.replace("/",
                "")).replace("آ", "ا"))) {

            Toast.makeText(getContext(), "answer is right nigga", Toast.LENGTH_LONG);
        }

    }
}

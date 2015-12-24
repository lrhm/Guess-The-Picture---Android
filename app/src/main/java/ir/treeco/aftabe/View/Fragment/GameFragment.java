package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Adapter.CoinAdapter;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.KeyboardAdapter;
import ir.treeco.aftabe.Adapter.SolutionAdapter;
import ir.treeco.aftabe.Interface.FinishLevel;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.CheatDrawable;
import ir.treeco.aftabe.View.Custom.KeyboardView;
import ir.treeco.aftabe.View.Dialog.FinishDailog;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Dialog.ImageFullScreenDialog;

public class GameFragment extends Fragment implements View.OnClickListener, KeyboardView.OnKeyboardEvent {
    private int levelId;
    private ImageView imageView;
    private int packageId;
    private Tools tools;
    private String status;
    private char[] statusAdapter;
    private char[] keyboardChars;
    private char[] solutionAdapter;
    private SolutionAdapter solutionAdapter0;
    private SolutionAdapter solutionAdapter1;
    private SolutionAdapter solutionAdapter2;
    private int break0;
    private int break1;
    private int[] sAndkIndex;  //!! should use hashMap
    private KeyboardAdapter keyboardAdapter;
    private int[] keyboardStatus;
    private Random random;
    private boolean[] keyboardB;
    private DBAdapter db;
    private Level level;
    private View view;
    private View[] cheatButtons;
    private View blackWidow;
    private String solution;
    private int solutionSize;
    private int packageSize;
    private GameFragment gameFragment;
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
        coinAdapter = new CoinAdapter(getActivity());
        lengthManager = ((MainApplication) getActivity().getApplication()).getLengthManager();
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        ((MainActivity) getActivity()).setupCheatButton(packageId);

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

    public void selectKeyboard(int adapterPosition) {
        for (int i = 0; i < statusAdapter.length; i++) {
            if (statusAdapter[i] == '-') {
                statusAdapter[i] = keyboardChars[adapterPosition];
                sAndkIndex[i] = adapterPosition;
                keyboardStatus[adapterPosition] = 1;

                keyboardAdapter.notifyDataSetChanged();

                if (i <= break0) {
                    solutionAdapter0.notifyDataSetChanged();
                } else if (i <= break1) {
                    solutionAdapter1.notifyDataSetChanged();
                } else {
                    solutionAdapter2.notifyDataSetChanged();
                }
                cheakSolotion();
                return;
            }
        }
    }

    public void removeFromSolution(int adapterPosition, int keyboard) {
        statusAdapter[adapterPosition] = '-';
        keyboardStatus[sAndkIndex[adapterPosition]] = keyboard;
        keyboardAdapter.notifyDataSetChanged();

        if (adapterPosition <= break0) {
            solutionAdapter0.notifyDataSetChanged();
        } else if (adapterPosition <= break1) {
            solutionAdapter1.notifyDataSetChanged();
        } else {
            solutionAdapter2.notifyDataSetChanged();
        }
        cheakSolotion();
    }

    private int getBreak(char[] string, int n) {
        int number = n;
        for (int i = 0; i < string.length; i++) {
            if (string[i] == '.') {
                if (number == 0) {
                    return i;
                } else {
                    number--;
                }
            }
        }
        return n;
    }


    private int getWidth(char[] string, int n) {
        int number = n - 1;
        int width = 0;
        for (int i = 0; i < string.length; i++) {
            if (string[i] == ' ') {
                width += solutionSize / 2;
            } else if (string[i] != '.') {
                width += solutionSize;
            } else if (string[i] == '.') {
                if (number == 0) {
                    return width;
                } else {
                    width = 0;
                    number--;
                }
            }
        }
        return width;
    }

    private void cheatHazf() {
        if (level.isResolved()) {
//            hazf();
            keyboardView.removeSome();
        } else if (coinAdapter.spendCoins(CoinAdapter.ALPHABET_HIDING_COST)) {
//            hazf();
            keyboardView.removeSome();
        }

    }

    private void hazf() {
        ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            if (!keyboardB[i] && keyboardStatus[i] != 2) {
                array.add(i);
            }
        }

        for (int i = 0; i < 7 && 0 < array.size(); i++) {

            int j = random.nextInt(array.size());
            if (keyboardStatus[array.get(j)] == 1) {
                for (int n = 0; n < sAndkIndex.length; n++) {
                    if (sAndkIndex[n] == array.get(j)) {
                        removeFromSolution(n, 2);
                        break;
                    }
                }
            } else {
                keyboardStatus[array.get(j)] = 2;
                keyboardAdapter.notifyDataSetChanged();
            }
            array.remove(j);
        }
    }

    private void cheatAzafe() { //is bad algoritm
        if (level.isResolved()) {
            keyboardView.showOne();
//            ezafe();
        } else if (coinAdapter.spendCoins(CoinAdapter.LETTER_REVEAL_COST)) {
//            ezafe();
            keyboardView.showOne();
        }

    }

    private void ezafe() {
        int rand;
        while (true) {
            rand = random.nextInt(statusAdapter.length);

            if (statusAdapter[rand] != '*' && statusAdapter[rand] != ' ' && statusAdapter[rand] != '.') {

                if (statusAdapter[rand] == '-') {
                    break;
                } else {
                    if (solutionAdapter[rand] != statusAdapter[rand]) {
                        removeFromSolution(rand, 0);
                        break;
                    }
                }
            }
        }

        statusAdapter[rand] = '*';

        boolean key = false;

        for (int i = 0; i < keyboardChars.length; i++) {
            if (keyboardChars[i] == solutionAdapter[rand] && keyboardStatus[i] == 0) {
                keyboardStatus[i] = 1;
                keyboardAdapter.notifyDataSetChanged();
                key = true;
                break;
            }
        }

        if (!key) {
            for (int i = 0; i < statusAdapter.length; i++) {
                if (statusAdapter[i] == solutionAdapter[rand]) {
                    if (statusAdapter[i] != '*' && statusAdapter[i] != solutionAdapter[i]) {
                        removeFromSolution(i, 1);
                        break;
                    }
                }
            }
        }

        if (rand <= break0) {
            solutionAdapter0.notifyDataSetChanged();
        } else if (rand <= break1) {
            solutionAdapter1.notifyDataSetChanged();
        } else {
            solutionAdapter2.notifyDataSetChanged();
        }
        cheakSolotion();
    }

    private void setUpImagePlace() {
        FrameLayout box = (FrameLayout) view.findViewById(R.id.box);
        tools.resizeView(box, lengthManager.getLevelImageWidth(), lengthManager.getLevelImageHeight());

        ImageView frame = (ImageView) view.findViewById(R.id.frame);
        frame.setImageBitmap(imageManager.loadImageFromResource(R.drawable.frame, lengthManager.getLevelImageFrameWidth(), lengthManager.getLevelImageFrameHeight()));
        tools.resizeView(frame, lengthManager.getLevelImageFrameWidth(), lengthManager.getLevelImageFrameHeight());

        cheatButtons = new View[]{
                view.findViewById(R.id.cheat_remove_some_letters),
                view.findViewById(R.id.cheat_reveal_a_letter),
                view.findViewById(R.id.cheat_skip_level)
        };

        for (View cheatView : cheatButtons) {
            cheatView.setOnClickListener(this);

            ViewGroup.LayoutParams layoutParams = cheatView.getLayoutParams();
            layoutParams.width = lengthManager.getCheatButtonWidth();
            layoutParams.height = lengthManager.getCheatButtonHeight();
        }

        String[] cheatTitles = new String[]{
                "حذف چند حرف",
                "نمایش یک حرف",
                "رد کردن مرحله"
        };

        int[] cheatCosts = new int[]{
                CoinAdapter.ALPHABET_HIDING_COST,
                CoinAdapter.LETTER_REVEAL_COST,
                CoinAdapter.SKIP_LEVEL_COST
        };

        for (int i = 0; i < 3; i++)
            tools.setViewBackground(
                    cheatButtons[i],
                    new CheatDrawable(  // TODO: 8/7/15 bad performance
                            view.getContext(),
                            i,
                            cheatTitles[i],
                            level.isResolved() ? "مفت" : tools.numeralStringToPersianDigits("" + cheatCosts[i])
                    )
            );


        blackWidow = view.findViewById(R.id.black_widow);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.cheat_remove_some_letters:
                ((MainActivity) getActivity()).toggleCheatButton();
                cheatHazf();
                break;

            case R.id.cheat_reveal_a_letter:
                ((MainActivity) getActivity()).toggleCheatButton();
                cheatAzafe();
                break;

            case R.id.cheat_skip_level:
                ((MainActivity) getActivity()).toggleCheatButton();
                cheatNext();
                break;

            case R.id.image_game:
                new ImageFullScreenDialog(getContext(), imagePath).show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).hideCheatButton();
    }

    public void showCheats() {
        for (View view : cheatButtons)
            view.setVisibility(View.VISIBLE);

        blackWidow.setVisibility(View.VISIBLE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(blackWidow, "alpha", 0, 0.60f),
                ObjectAnimator.ofFloat(cheatButtons[0], "translationX", -cheatButtons[0].getWidth(), 0),
                ObjectAnimator.ofFloat(cheatButtons[1], "translationX", +cheatButtons[1].getWidth(), 0),
                ObjectAnimator.ofFloat(cheatButtons[2], "translationX", -cheatButtons[2].getWidth(), 0)
        );


        animatorSet.setDuration(600).start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ((MainActivity) getActivity()).disableCheatButton(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public void hideCheats() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(blackWidow, "alpha", 0.60f, 0),
                ObjectAnimator.ofFloat(cheatButtons[0], "translationX", 0, -cheatButtons[0].getWidth()),
                ObjectAnimator.ofFloat(cheatButtons[1], "translationX", 0, +cheatButtons[1].getWidth()),
                ObjectAnimator.ofFloat(cheatButtons[2], "translationX", 0, -cheatButtons[2].getWidth())
        );

        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                for (View view : cheatButtons) {
                    view.setVisibility(View.GONE);
                    view.clearAnimation();
                }

                blackWidow.setVisibility(View.GONE);
                ((MainActivity) getActivity()).disableCheatButton(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animatorSet.setDuration(600).start();
    }

    private boolean cheakSolotion() {
        for (int i = 0; i < solutionAdapter.length; i++) {
            if (solutionAdapter[i] != statusAdapter[i] && statusAdapter[i] != '*') {
                return false;
            }
        }

        if (!level.isResolved()) {
            coinAdapter.earnCoins(CoinAdapter.LEVEL_COMPELETED_PRIZE);
        }

        nextLevel();
        return true;
    }

    private void cheatNext() {
        if (level.isResolved()) {
            nextLevel();
        } else if (coinAdapter.spendCoins(CoinAdapter.SKIP_LEVEL_COST)) {
            nextLevel();
        }
    }

    private void nextLevel() {
        db.resolveLevel(packageId, levelId);
        tools.backUpDB();
        new FinishDailog(getActivity(), level, packageSize,
                new FinishLevel() {
                    @Override
                    public void NextLevel() {
                        Bundle bundle = new Bundle();
                        int levelID = level.getId() + 1;
                        bundle.putInt("LevelId", levelID);
                        bundle.putInt("id", packageId);

                        GameFragment gameFragment = new GameFragment();
                        gameFragment.setArguments(bundle);

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, gameFragment, "GameFragment");
                        transaction.commit();
                    }

                    @Override
                    public void Home() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }).show();
    }

    @Override
    public void onHintClicked() {

    }

    @Override
    public void onAllAnswered(String guess) {

        if ((guess.replace("آ", "ا")).equals((solution.replace("/",
                "")).replace("آ", "ا"))) {
            if (!level.isResolved()) {
                coinAdapter.earnCoins(CoinAdapter.LEVEL_COMPELETED_PRIZE);
            }

            nextLevel();

        }

    }
}
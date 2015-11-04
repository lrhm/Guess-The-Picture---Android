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
import ir.treeco.aftabe.View.Dialog.FinishDailog;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Dialog.ImageFullScreenDialog;

public class GameFragment extends Fragment implements View.OnClickListener {
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

        ((MainActivity)getActivity()).setupCheatButton(packageId);

        levelId = getArguments().getInt("LevelId");
        packageId = getArguments().getInt("id");

        level = db.getLevel(packageId, levelId);
        packageSize = db.getLevels(packageId).length;

        solution = tools.decodeBase64(level.getJavab());
        StringBuilder stringBuilder = new StringBuilder(solution);

        setUpImagePlace();

        for (int i = 0; i < solution.length(); i++) {
            if (solution.charAt(i) != '.' && solution.charAt(i) != ' ') {
                stringBuilder.setCharAt(i, '-');
            }
        }

        status = String.valueOf(stringBuilder);

        Log.e("solotion", solution);

        solutionAdapter = solution.toCharArray();
        statusAdapter = status.toCharArray();
        sAndkIndex = new int[statusAdapter.length];

        if (solution.length() > 12) {
            break0 = getBreak(solutionAdapter, 0);

            if (solution.length() > 24) {
                break1 = getBreak(solutionAdapter, 1);
            } else {
                break1 = solution.length();
            }
        } else {
            break0 = solution.length();
            break1 = 0;
        }

        solutionSize = lengthManager.getSolutionButtonSize();

        RecyclerView recyclerView_solution1 =
                (RecyclerView) view.findViewById(R.id.recycler_view_solution1);

        recyclerView_solution1.setHasFixedSize(true);

        recyclerView_solution1.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));

        solutionAdapter0 = new SolutionAdapter(lengthManager, solutionAdapter, this, statusAdapter, 0, break0, break1);
        recyclerView_solution1.setAdapter(solutionAdapter0);

        ViewGroup.LayoutParams layoutParams = recyclerView_solution1.getLayoutParams();
        layoutParams.width = getWidth(solutionAdapter, 1);
//        layoutParams.height = solutionSize;

        if (solutionAdapter.length > 12) {
            RecyclerView recyclerView_solution2 =
                    (RecyclerView) view.findViewById(R.id.recycler_view_solution2);

            recyclerView_solution2.setHasFixedSize(true);

            recyclerView_solution2.setLayoutManager(
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));

            solutionAdapter1 = new SolutionAdapter(lengthManager, solutionAdapter, this, statusAdapter, 1, break0, break1);
            recyclerView_solution2.setAdapter(solutionAdapter1);

            ViewGroup.LayoutParams layoutParams2 = recyclerView_solution2.getLayoutParams();
            layoutParams2.width = getWidth(solutionAdapter, 2);
            layoutParams2.height = solutionSize;

            recyclerView_solution2.setVisibility(View.VISIBLE);
        }

        if (solutionAdapter.length > 24) {
            RecyclerView recyclerView_solution3 = (
                    RecyclerView) view.findViewById(R.id.recycler_view_solution3);

            recyclerView_solution3.setHasFixedSize(true);

            recyclerView_solution3.setLayoutManager(
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));

            solutionAdapter2 = new SolutionAdapter(lengthManager, solutionAdapter, this, statusAdapter, 2, break0, break1);
            recyclerView_solution3.setAdapter(solutionAdapter2);

            ViewGroup.LayoutParams layoutParams3 = recyclerView_solution3.getLayoutParams();
            layoutParams3.width = getWidth(solutionAdapter, 3);
            layoutParams3.height = solutionSize;


            recyclerView_solution3.setVisibility(View.VISIBLE);
        }

        char[] alphabet = {
                'ی', 'ه','و' , 'ن', 'م', 'ل', 'گ', 'ک', 'ق', 'ف', 'غ', 'ع', 'ظ', 'ط', 'ض', 'ص', 'ش',
                'س','ژ' , 'ز', 'ر', 'ذ','د' , 'خ', 'ح', 'چ', 'ج', 'ث', 'ت', 'پ', 'ب', 'ا', 'آ' };

        keyboardChars = new char[21];
        keyboardStatus = new int[21];
        keyboardB = new boolean[21];

        ArrayList<Integer> list = new ArrayList<>();
        random = new Random();
        for (int i = 0; i < 21; i++) {
            keyboardChars[i] = alphabet[random.nextInt(33)];
            list.add(i);
        }

        for (int i = 0; i < solutionAdapter.length; i++) { //fuck
            if (solutionAdapter[i] != ' ' && solutionAdapter[i] != '.') {
                int j = random.nextInt(list.size());
                keyboardChars[list.get(j)] = solutionAdapter[i];
                keyboardB[list.get(j)] = true;
                list.remove(j);
            }
        }

        RecyclerView recyclerView_keyboard = (
                RecyclerView) view.findViewById(R.id.recycler_view_keyboard);

        recyclerView_keyboard.setHasFixedSize(true);
        recyclerView_keyboard.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        keyboardAdapter = new KeyboardAdapter(this, keyboardChars, keyboardStatus);
        recyclerView_keyboard.setAdapter(keyboardAdapter);

        imageView = (ImageView) view.findViewById(R.id.image_game);
        imageView.setOnClickListener(this);
//        ImageView imageView_game_frame = (ImageView) findViewById(R.id.image_game_frame);
//        imageView_game_frame.setBackgroundResource(R.drawable.frame);

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
                } else if(i <= break1) {
                    solutionAdapter1.notifyDataSetChanged();
                } else {
                    solutionAdapter2.notifyDataSetChanged();
                }
                cheakSolotion();
                return;
            }
        }
    }

    public void removeFromSolution (int adapterPosition, int keyboard) {
        statusAdapter[adapterPosition] = '-';
        keyboardStatus[sAndkIndex[adapterPosition]] = keyboard;
        keyboardAdapter.notifyDataSetChanged();

        if (adapterPosition <= break0) {
            solutionAdapter0.notifyDataSetChanged();
        } else if(adapterPosition <= break1) {
            solutionAdapter1.notifyDataSetChanged();
        } else {
            solutionAdapter2.notifyDataSetChanged();
        }
        cheakSolotion();
    }

    private int getBreak (char[] string, int n) {
        int number = n;
        for (int i = 0; i < string.length; i++) {
            if (string[i] == '.'){
                if (number == 0) {
                    return i;
                } else {
                    number--;
                }
            }
        }
        return n;
    }


    private int getWidth (char[] string, int n) {
        int number = n - 1;
        int width = 0;
        for (int i = 0; i < string.length; i++) {
            if (string[i] == ' ') {
                width += solutionSize/2;
            } else if (string[i] != '.') {
                width += solutionSize;
            } else if (string[i] == '.'){
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
            hazf();
        } else if (coinAdapter.spendCoins(CoinAdapter.ALPHABET_HIDING_COST)) {
            hazf();
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
                for (int n = 0; n < sAndkIndex.length; n++){
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
            ezafe();
        } else if (coinAdapter.spendCoins(CoinAdapter.LETTER_REVEAL_COST)) {
            ezafe();
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

        cheatButtons = new View[] {
                view.findViewById(R.id.cheat_remove_some_letters),
                view.findViewById(R.id.cheat_reveal_a_letter),
                view.findViewById(R.id.cheat_skip_level)
        };

        for (View cheatView: cheatButtons) {
            cheatView.setOnClickListener(this);

            ViewGroup.LayoutParams layoutParams = cheatView.getLayoutParams();
            layoutParams.width = lengthManager.getCheatButtonWidth();
            layoutParams.height = lengthManager.getCheatButtonHeight();
        }

        String[] cheatTitles = new String[] {
                "حذف چند حرف",
                "نمایش یک حرف",
                "رد کردن مرحله"
        };

        int[] cheatCosts = new int[] {
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
        ((MainActivity)getActivity()).hideCheatButton();
    }

    public void showCheats() {
        for (View view: cheatButtons)
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
                        transaction.replace(R.id.fragment_container, gameFragment,"GameFragment");
                        transaction.commit();
                    }

                    @Override
                    public void Home() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }).show();
    }

}

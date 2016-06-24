package ir.treeco.aftabe2.View.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import ir.treeco.aftabe2.Adapter.LevelsAdapter;
import ir.treeco.aftabe2.Util.Logger;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ir.treeco.aftabe2.Adapter.Cache.PackageSolvedCache;
import ir.treeco.aftabe2.Adapter.MediaAdapter;
import ir.treeco.aftabe2.Adapter.TimeStampAdapter;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Adapter.CoinAdapter;
import ir.treeco.aftabe2.Adapter.DBAdapter;
import ir.treeco.aftabe2.Interface.FinishLevel;
import ir.treeco.aftabe2.Object.Level;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.CheatDrawable;
import ir.treeco.aftabe2.View.Custom.KeyboardView;
import ir.treeco.aftabe2.View.Custom.ToastMaker;
import ir.treeco.aftabe2.View.Dialog.DialogAdapter;
import ir.treeco.aftabe2.View.Dialog.FinishDailog;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.View.Dialog.ImageFullScreenDialog;

public class GameFragment extends Fragment implements View.OnClickListener, KeyboardView.OnKeyboardEvent {
    private static final String TAG = "GameFragment";
    private int levelId;
    private ImageView imageView;
    private int packageId;
    private Tools tools;
    private boolean resulved = false;
    private DBAdapter db;
    private Level level;
    private View view;
    private View[] cheatButtons;
    private View blackWidow;
    private String solution;
    private int packageSize;
    private CoinAdapter coinAdapter;
    private LengthManager lengthManager;
    private ImageManager imageManager;
    private String imagePath;
    private KeyboardView keyboardView;
    private TimeStampAdapter timeStampAdapter;
    private boolean skiped = false;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        Logger.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_game, container, false);

        mainActivity = (MainActivity) getActivity();

        timeStampAdapter = new TimeStampAdapter();
        tools = new Tools(getContext());
        db = DBAdapter.getInstance(getActivity());
        coinAdapter = new CoinAdapter(getActivity(), getActivity());
        lengthManager = ((MainApplication) getActivity().getApplication()).getLengthManager();
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        ((MainActivity) getActivity()).setupCheatButton(packageId);

        levelId = getArguments().getInt("LevelId");
        packageId = getArguments().getInt("id");

        level = db.getLevel(packageId, levelId);
        packageSize = db.getLevels(packageId).length;

        solution = tools.decodeBase64(level.getJavab());

        Answers.getInstance().logLevelStart(new LevelStartEvent()
                .putLevelName(solution)
                .putCustomAttribute("Package", packageId)
                .putCustomAttribute("Package Name", "package " + packageId)
                .putCustomAttribute("Solved Before", (level.isResolved() ? 1 : 0))
        );

//        Answers.getInstance().logCustom(
//                new CustomEvent("Package Play")
//                        .putCustomAttribute("PackageID", packageId)
//                        .putCustomAttribute("PackageName", "package " + packageId));

        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fragment_game_keyboard_container);

        keyboardView = new KeyboardView(getContext(), solution);
        keyboardView.onKeyboardEvent = this;
        frameLayout.addView(keyboardView);
        setUpImagePlace();

        imageView = (ImageView) view.findViewById(R.id.image_game);
        imageView.setOnClickListener(this);

        imagePath = "file://" + getActivity().getFilesDir().getPath() + "/Packages/package_" + packageId + "/" + level.getResources();

        Picasso.with(getActivity()).load(imagePath).into(imageView);

        setupForOutsideOfCheat(view);


        if (packageId == 0 && levelId == 0)
            DialogAdapter.makeTutorialDialog(getContext(),
                    "شما توی این بازی یک عبارت تقریبا بی\u200Cربط را از روی عکس هر مرحله حدس می\u200Cزنید. برای مثال در این مرحله یک خر که به رنگ آبی درامده را می بینید. "
                    , "پس جواب این مرحله می شود \"خرابی\" خر+رنگ آبی");
        return view;
    }


    private void cheatHazf() {
        if (level.isResolved()) {
            keyboardView.removeSome();
        } else if (coinAdapter.spendCoins(CoinAdapter.ALPHABET_HIDING_COST)) {
            if (!keyboardView.removeSome()) {
                String toastText = "نمیشه دیگه";
                ToastMaker.show(getContext(), toastText, Toast.LENGTH_SHORT);
                coinAdapter.earnCoins(CoinAdapter.ALPHABET_HIDING_COST);
            }
        }

    }


    private void cheatAzafe() {
        if (level.isResolved()) {
            keyboardView.showOne();
        } else if (coinAdapter.spendCoins(CoinAdapter.LETTER_REVEAL_COST)) {
            if (!keyboardView.showOne()) {
                String toastText = "نمیشه دیگه";
                ToastMaker.show(getContext(), toastText, Toast.LENGTH_SHORT);
                coinAdapter.earnCoins(CoinAdapter.LETTER_REVEAL_COST);
            }
        }

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


    private long lastTimeClicked = 0;
    private long treshHold = 850;

    @Override
    public void onClick(View view) {

        long clickTime = System.currentTimeMillis();
        if (clickTime - lastTimeClicked < treshHold)
            return;

        Logger.d(TAG, "last time is " + lastTimeClicked);
        Logger.d(TAG, "current time is " + clickTime);

        lastTimeClicked = clickTime;

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
        ((MainActivity) getActivity()).hideCheatButton();
        if (level != null) {

            try {
                Answers.getInstance().logLevelEnd(new LevelEndEvent()
                        .putLevelName(solution)
                        .putSuccess(resulved)
                        .putCustomAttribute("Solved Before", (level.isResolved() ? 1 : 0))
                        .putCustomAttribute("Package", packageId)
                        .putCustomAttribute("Package Name", "package " + packageId)
                        .putCustomAttribute("Time", timeStampAdapter.getTimeStamp(getActivity())));
            } catch (Exception e) {

            }
        }
        super.onDestroy();

        Logger.d(TAG, "onDestory");
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
                if (getActivity() == null)
                    return;


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
                if (getActivity() == null)
                    return;


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

    @Override
    public void onDetach() {

        if (mainActivity != null)
            (mainActivity).disableCheatButton(true);

        super.onDetach();
    }

    private void cheatNext() {
        if (level.isResolved()) {
            nextLevel();
        } else if (coinAdapter.spendCoins(CoinAdapter.SKIP_LEVEL_COST)) {
            skiped = true;
            resulved = true;
            nextLevel();
        }
    }

    private void nextLevel() {
        db.resolveLevel(packageId, levelId);
//        tools.backUpDB();
        new FinishDailog(getActivity(), level, packageSize, skiped,
                new FinishLevel() {
                    @Override
                    public void NextLevel() {


                        Bundle bundle = new Bundle();
                        int levelID = level.getId() + 1;
                        bundle.putInt("LevelId", levelID);
                        bundle.putInt("id", packageId);

                        GameFragment gameFragment = new GameFragment();
                        gameFragment.setArguments(bundle);

                        if (mainActivity == null && getActivity() != null)
                            mainActivity = (MainActivity) getActivity();
                        if (mainActivity != null) {
                            FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, gameFragment, LevelsAdapter.OFFLINE_GAME_FRAGMENT_TAG);
                            transaction.commitAllowingStateLoss();

                        }
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

        if ((guess.replace("آ", "ا").replace("/", "")).equals((solution.replace(".",
                "")).replace("آ", "ا").replace("/", ""))) {
            MediaAdapter.getInstance(getContext()).playCorrectSound();

            if (!level.isResolved() && !resulved) {
                coinAdapter.earnCoins(CoinAdapter.LEVEL_COMPELETED_PRIZE);
                resulved = true;

                PackageSolvedCache.getInstance().onNewLevelSolved(packageId, levelId);
                PackageSolvedCache.getInstance().updateToServer();
            }

            nextLevel();

        }

    }


    @Override
    public void onAttach(Context context) {
        Logger.d(TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        Logger.d(TAG, "onPause");

        timeStampAdapter.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Logger.d(TAG, "onDestroyView");
        super.onDestroyView();
    }


    @Override
    public void onResume() {
        Logger.d(TAG, "onResume");
        timeStampAdapter.onResume();
        super.onResume();
    }


    private void setupForOutsideOfCheat(View view) {

        ArrayList<Integer> cheats = new ArrayList<>();
        cheats.add(R.id.cheat_skip_level);
        cheats.add(R.id.cheat_remove_some_letters);
        cheats.add(R.id.cheat_reveal_a_letter);

        if (view.getId() == R.id.level_view || cheats.contains(view.getId()))
            return;


        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (((MainActivity) getActivity()).isCheatsVisible()) {

                    ((MainActivity) getActivity()).toggleCheatButton();

                }
                return false;
            }

        });


        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupForOutsideOfCheat(innerView);
            }
        }
    }
}

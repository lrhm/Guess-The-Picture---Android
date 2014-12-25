package ir.treeco.aftabe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import ir.treeco.aftabe.mutlimedia.Multimedia;
import ir.treeco.aftabe.packages.Level;
import ir.treeco.aftabe.utils.*;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by hamed on 9/2/14.
 */
public class LevelFragment extends Fragment {
    Level mLevel;
    private String[] alphabet;
    private ImageView[] alphabetButtons;
    private String[] solution;
    private ImageView[] solutionButtons;
    private SharedPreferences preferences;
    private Level.AlphabetState[] alphabetGone;
    private int[] placeHolder;
    private Context mContext;
    Typeface buttonFont;
    private ImageView flyingButton;
    private View[] cheatButtons;
    private View blackWidow;
    private Multimedia[] resources;
    private LayoutInflater inflater;
    private LinearLayout greetingsView = null;
    private ImageView cheatButton;
    private Bitmap cheatBitmap;
    private Bitmap backBitmap;
    private boolean areCheatsVisible = false;


    public static LevelFragment newInstance(Level mLevel) {
        LevelFragment levelFragment = new LevelFragment();
        levelFragment.mLevel = mLevel;
        return levelFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().findViewById(R.id.logo).setVisibility(View.INVISIBLE);
        this.inflater = inflater;

        final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_level, container, false);

        mContext = getActivity();
        preferences = mContext.getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        buttonFont = FontsHolder.getTabBarFont(mContext);

        alphabet = mLevel.getAlphabetLabels();
        alphabetButtons = new ImageView[alphabet.length];
        solution = mLevel.getSolutionLabels();
        solutionButtons = new ImageView[solution.length];

        try {
            alphabetGone = mLevel.getAlphabetGone(preferences);
            placeHolder = mLevel.getPlaceHolder(preferences);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        loadResources(layout);

        setUpFlyingButton((FrameLayout) layout);
        setUpSolutionLinearLayout(inflater, layout);
        setUpAlphabetLinearLayout(inflater, layout);
        setUpImagePlace(layout);
        setUpCheatLayout(layout);
        setupCheatButton(layout);

        {
            FragmentManager fragmentManager = getFragmentManager();
            int length = fragmentManager.getBackStackEntryCount();
            String result = "Path: ";
            for (int i = 0; i < length; i++) {
                FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(i);
                result += entry.getBreadCrumbShortTitle() + "|" + entry.getBreadCrumbTitle() + ", ";
            }
            Log.e("BREAD", result);
        }

        return layout;
    }

    private void loadResources(final View view) {
        LoadingManager.startTask(new TaskStartedListener() {
            @Override
            public void taskStarted() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        resources = mLevel.getResources();
                        int index = 0;
                        for (Multimedia resource: resources) {
                            try {
                                File file = new File(getActivity().getCacheDir(), "mm_" + index);
                                OutputStream os = new FileOutputStream(file);
                                InputStream is = resource.getMedia();
                                Utils.pipe(is, os);
                                os.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            index++;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ViewPager viewPager = (ViewPager) view.findViewById(R.id.multimedia);
                                viewPager.setAdapter(new MultimediaAdapter(LevelFragment.this));

                                LoadingManager.endTask();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void setupCheatButton(View layout) {
        float[] cheatButtonHSV = mLevel.getWrapperPackage().meta.getCheatButtonHSV();
        cheatBitmap = Utils.updateHSV(ImageManager.loadImageFromResource(layout.getContext(), R.drawable.cheat_button, LengthManager.getCheatButtonSize(), LengthManager.getCheatButtonSize()), cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);
        backBitmap = Utils.updateHSV(ImageManager.loadImageFromResource(layout.getContext(), R.drawable.back_button, LengthManager.getCheatButtonSize(), LengthManager.getCheatButtonSize()), cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);

        cheatButton = (ImageView) getActivity().findViewById(R.id.cheat_button);
        cheatButton.setVisibility(View.VISIBLE);
        cheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (!areCheatsVisible) {
                    showCheats();
                } else {
                    hideCheats();
                }
            }
        });

        cheatButton.setImageBitmap(cheatBitmap);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cheatButton.getLayoutParams();
        layoutParams.leftMargin = (int) (0.724 * LengthManager.getScreenWidth());
        layoutParams.topMargin = (int) (0.07 * LengthManager.getScreenWidth());
    }

    private void setUpCheatLayout(View layout) {
        View[] buttons = new View[] {
                layout.findViewById(R.id.cheat_remove_some_letters),
                layout.findViewById(R.id.cheat_reveal_a_letter),
                layout.findViewById(R.id.cheat_skip_level)
        };
        for (int i = 0; i < buttons.length; i++) {
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doCheat(finalI);
                    hideCheats();
                }
            });
        }
    }

    void makeCheatFailedToast(String message) {
        ToastMaker.show(mContext, message, Toast.LENGTH_LONG);
    }

    void doCheat(int id) {
        try {
            if (id == 0) cheatAndRemoveSomeLetters();
            if (id == 1) cheatAndRevealALetter();
            if (id == 2) cheatAndSkipLevel();
        } catch (NotEnoughMoneyException e) {
            makeCheatFailedToast("پول ندارید :(");
            return;
        } catch (JSONException e) {
            makeCheatFailedToast("مشکل غیر منتظره‌ای پیش آمده است. لطفا آفتابه را به روز رسانی کنید.");
            return;
        } catch (ImpossibleCheatException e) {
            makeCheatFailedToast("نمی‌شه دیگه!");
            return;
        }
        if (id == 2 || id == 3) {
            checkLevelCompleted(false);
        }
    }


    public void cheatAndSkipLevel() throws JSONException, NotEnoughMoneyException {
        if (!mLevel.isSolved() && CoinManager.getCoinsCount(preferences) < CoinManager.SKIP_LEVEL_COST)
            throw new NotEnoughMoneyException();

        Arrays.fill(placeHolder, -1);
        for (int i = 0; i < alphabetGone.length; i++)
            alphabetGone[i] = Level.AlphabetState.IN_THERE;

        for (int i = 0; i < alphabet.length; i++)
            updateAlphabet(i);

        int order[] = Utils.getRandomOrder(alphabet.length, null);

        for (int i: order)
            for (int j = 0; j < solution.length; j++)
                if (placeHolder[j] == -1 && alphabet[i].equals(solution[j])) {
                    placeHolder[j] = i;
                    alphabetGone[i] = Level.AlphabetState.CLICKED;
                    updateAlphabet(i);
                    updateSolution(j);
                    break;
                }

        if (!mLevel.isSolved()) {
            CoinManager.spendCoins(CoinManager.SKIP_LEVEL_COST, preferences);
        }

        mLevel.save(alphabetGone, placeHolder);
    }
    public void cheatAndRemoveSomeLetters() throws JSONException, NotEnoughMoneyException, ImpossibleCheatException {
        if (!mLevel.isSolved() && CoinManager.getCoinsCount(preferences) < CoinManager.ALPHABET_HIDING_COST)
            throw new NotEnoughMoneyException();

        int order[] = Utils.getRandomOrder(alphabet.length, null);

        int done = 0;

        for (int i: order) {
            if (alphabetGone[i] == Level.AlphabetState.FIXED || alphabetGone[i] == Level.AlphabetState.REMOVED)
                continue;

            int remainingCount = 0;
            int neededCount = 0;

            for (int j = 0; j < solution.length; j++) {
                if (solution[j].equals(alphabet[i])) {
                    if (placeHolder[j] != -1 && alphabetGone[placeHolder[j]] == Level.AlphabetState.FIXED)
                        continue;
                    neededCount++;
                }
            }

            for (int j = 0; j < alphabet.length; j++) {
                if (alphabetGone[j] == Level.AlphabetState.FIXED || alphabetGone[j] == Level.AlphabetState.REMOVED)
                    continue;
                if (alphabet[j].equals(alphabet[i]))
                    remainingCount++;
            }

            if (neededCount > remainingCount) {
                throw new RuntimeException();
            }

            if (remainingCount == neededCount)
                continue;

            if (done < 7) {
                for (int j = 0; j < placeHolder.length; j++)
                    if (placeHolder[j] == i) {
                        placeHolder[j] = -1;
                        updateSolution(j);
                    }
                alphabetGone[i] = Level.AlphabetState.REMOVED;
                updateAlphabet(i);
                done++;
            }
        }

        //String log = "Here is alphabetGone:";
        //for (Level.AlphabetState state: alphabetGone)
        //    log += " " + state;
        //Log.i("GOLVAZHE", log);

        if (done == 0)
            throw new ImpossibleCheatException();

        if (!mLevel.isSolved()) {
            CoinManager.spendCoins(CoinManager.ALPHABET_HIDING_COST, preferences);
        }

        mLevel.save(alphabetGone, placeHolder);
    }
    public void cheatAndRevealALetter() throws JSONException, NotEnoughMoneyException, ImpossibleCheatException {
        if (!mLevel.isSolved() && CoinManager.getCoinsCount(preferences) < CoinManager.LETTER_REVEAL_COST)
            throw new NotEnoughMoneyException();

        Random random = new Random();
        ArrayList<Integer> positions = new ArrayList<Integer>();

        for (int i = 0; i < solution.length; i++) {
            if (solution[i].equals(" ") || solution[i].equals("."))
                continue;
            if (placeHolder[i] != -1 && (alphabetGone[placeHolder[i]] != Level.AlphabetState.IN_THERE || alphabetGone[placeHolder[i]] != Level.AlphabetState.CLICKED))
                continue;
            positions.add(i);
        }

        if (positions.isEmpty())
            throw new ImpossibleCheatException();

        int position = positions.get(random.nextInt(positions.size()));
        int place = -1;

        for (int i = 0; i < alphabet.length; i++)
            if ((alphabetGone[i] == Level.AlphabetState.IN_THERE || alphabetGone[i] == Level.AlphabetState.CLICKED) && solution[position].equals(alphabet[i])) {
                place = i;
                break;
            }

        int toBeUpdatedAlphabet = placeHolder[position];

        if (place == -1) {
            throw new RuntimeException();
        }

        for (int i = 0; i < placeHolder.length; i++)
            if (placeHolder[i] == place) {
                placeHolder[i] = -1;
                updateSolution(i);
            }

        alphabetGone[place] = Level.AlphabetState.FIXED;
        placeHolder[position] = place;

        if (toBeUpdatedAlphabet != -1)
            updateAlphabet(toBeUpdatedAlphabet);
        updateAlphabet(place);
        updateSolution(position);

        if (!mLevel.isSolved()) {
            CoinManager.spendCoins(CoinManager.LETTER_REVEAL_COST, preferences);
        }

        mLevel.save(alphabetGone, placeHolder);
    }


    private void setUpFlyingButton(FrameLayout view) {
        ImageView button = new ImageView(getActivity());

        button.setImageDrawable(new LetterButtonDrawable(null, getActivity()));

        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), ImageManager.loadImageFromResource(mContext, R.drawable.albutton, LengthManager.getAlphabetButtonSize(), LengthManager.getAlphabetButtonSize()));
        Utils.setViewBackground(button, bitmapDrawable);

        button.setVisibility(View.INVISIBLE);

        view.addView(button, new FrameLayout.LayoutParams(LengthManager.getAlphabetButtonSize(), LengthManager.getAlphabetButtonSize()));

        this.flyingButton = button;
    }

    private void setUpImagePlace(final View view) {
        //ViewPager viewPager = (ViewPager) view.findViewById(R.id.multimedia);
        //viewPager.setAdapter(new MultimediaAdapter(this));

        FrameLayout box = (FrameLayout) view.findViewById(R.id.box);
        Utils.resizeView(box, LengthManager.getLevelImageWidth(), LengthManager.getLevelImageHeight());

        ImageView frame = (ImageView) view.findViewById(R.id.frame);
        frame.setImageBitmap(ImageManager.loadImageFromResource(view.getContext(), R.drawable.frame, LengthManager.getLevelImageFrameWidth(), LengthManager.getLevelImageFrameHeight()));
        Utils.resizeView(frame, LengthManager.getLevelImageFrameWidth(), LengthManager.getLevelImageFrameHeight());

        Utils.resizeView(view.findViewById(R.id.level_view), ViewGroup.LayoutParams.MATCH_PARENT, LengthManager.getLevelImageFrameHeight());

        cheatButtons = new View[] {
                view.findViewById(R.id.cheat_remove_some_letters),
                view.findViewById(R.id.cheat_reveal_a_letter),
                view.findViewById(R.id.cheat_skip_level)
        };

        for (View cheatView: cheatButtons) {
            final ViewGroup.LayoutParams layoutParams = cheatView.getLayoutParams();
            layoutParams.width = LengthManager.getCheatButtonWidth();
            layoutParams.height = LengthManager.getCheatButtonHeight();
        }

        Bitmap cheatBack = ImageManager.loadImageFromResource(view.getContext(), R.drawable.cheat_right, LengthManager.getCheatButtonWidth(), -1);

        String[] cheatTitles = new String[] {
                "حذف چند حرف",
                "نمایش یک حرف",
                "رد کردن مرحله"
        };

        boolean isSolved = mLevel.isSolved();

        int[] cheatCosts = new int[] {
                CoinManager.ALPHABET_HIDING_COST,
                CoinManager.LETTER_REVEAL_COST,
                CoinManager.SKIP_LEVEL_COST
        };

        for (int i = 0; i < 3; i++)
            Utils.setViewBackground(cheatButtons[i], new CheatDrawable(view.getContext(), i, cheatBack, cheatTitles[i], isSolved? "مفت": Utils.numeralStringToPersianDigits("" + cheatCosts[i])));

        blackWidow = view.findViewById(R.id.black_widow);

        if (resources.length > 1) {
            final ImageView oneFingerDrag = (ImageView) view.findViewById(R.id.one_finger_drag);
            oneFingerDrag.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), R.drawable.one_finger_drag, LengthManager.getOneFingerDragWidth(),  -1));
            oneFingerDrag.setVisibility(View.VISIBLE);

            int translationX = LengthManager.getOneFingerDragWidth() * 130 / 100;
            ObjectAnimator animator = ObjectAnimator.ofFloat(oneFingerDrag, "translationX", +translationX, -translationX).setDuration(2000);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    oneFingerDrag.setVisibility(View.GONE);
                    oneFingerDrag.clearAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            animator.start();
        }
    }

    public void showCheats() {
        areCheatsVisible = true;
        cheatButton.setImageBitmap(backBitmap);

        for (View view: cheatButtons)
            view.setVisibility(View.VISIBLE);

        AnimatorSet set = new AnimatorSet();

        blackWidow.setVisibility(View.VISIBLE);

        set.playTogether(
                ObjectAnimator.ofFloat(blackWidow, "alpha", 0, 0.60f),
                ObjectAnimator.ofFloat(cheatButtons[0], "translationX", -cheatButtons[0].getWidth(), 0),
                ObjectAnimator.ofFloat(cheatButtons[1], "translationX", +cheatButtons[1].getWidth(), 0),
                ObjectAnimator.ofFloat(cheatButtons[2], "translationX", -cheatButtons[2].getWidth(), 0)
        );

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                cheatButton.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                cheatButton.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(600).start();
    }

    public void hideCheats() {
        areCheatsVisible = false;
        cheatButton.setImageBitmap(cheatBitmap);


        AnimatorSet set = new AnimatorSet();

        set.playTogether(
                ObjectAnimator.ofFloat(blackWidow, "alpha", 0.60f, 0),
                ObjectAnimator.ofFloat(cheatButtons[0], "translationX", 0, -cheatButtons[0].getWidth()),
                ObjectAnimator.ofFloat(cheatButtons[1], "translationX", 0, +cheatButtons[1].getWidth()),
                ObjectAnimator.ofFloat(cheatButtons[2], "translationX", 0, -cheatButtons[2].getWidth())
        );

        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                cheatButton.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                for (View view: cheatButtons) {
                    view.setVisibility(View.GONE);
                    view.clearAnimation();
                }

                blackWidow.setVisibility(View.GONE);
                cheatButton.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        set.setDuration(600).start();
    }

    private void setUpAlphabetLinearLayout(LayoutInflater inflater, View view) {
        final int alphabetButtonSize = LengthManager.getAlphabetButtonSize();
        final LinearLayout linesLayout = (LinearLayout) view.findViewById(R.id.alphabet_rows);
        linesLayout.removeAllViewsInLayout();


        for (int i = 0; i < 3; i++) {
            LinearLayout currentRow = new LinearLayout(mContext);
            currentRow.addView(Utils.makeNewSpace(mContext));
            for (int j = 0; j < 7; j++) {
                final int id = i * 7 + j;

                ImageView button = new ImageView(getActivity());

                button.setImageDrawable(new LetterButtonDrawable(alphabet[id], getActivity()));

                button.setLayoutParams(new LinearLayout.LayoutParams(alphabetButtonSize, alphabetButtonSize));

                currentRow.addView(button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alphabetClicked(id);
                        mLevel.save(alphabetGone, placeHolder);
                    }
                });

                alphabetButtons[id] = button;

                updateAlphabet(id);
            }
            currentRow.addView(Utils.makeNewSpace(mContext));
            linesLayout.addView(currentRow);
        }
    }

    public void updateAlphabet(int id) {
        ImageView button = alphabetButtons[id];

        if (alphabetGone[id] != Level.AlphabetState.IN_THERE) {
            button.setVisibility(View.INVISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), ImageManager.loadImageFromResource(mContext, R.drawable.albutton, LengthManager.getAlphabetButtonSize(), LengthManager.getAlphabetButtonSize()));
            Utils.setViewBackground(button, bitmapDrawable);
        }
    }

    public void updateSolution(int id) {
        ImageView button = solutionButtons[id];
        LetterButtonDrawable drawable = (LetterButtonDrawable) button.getDrawable();

        if (placeHolder[id] == -1) {
            drawable.setLabel(null);

            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), ImageManager.loadImageFromResource(mContext, R.drawable.place_holder, LengthManager.getSolutionButtonSize(), LengthManager.getSolutionButtonSize()));
            Utils.setViewBackground(button, bitmapDrawable);
        } else {
            drawable.setLabel(alphabet[placeHolder[id]]);
            drawable.setGreen(alphabetGone[placeHolder[id]] == Level.AlphabetState.FIXED);

            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), ImageManager.loadImageFromResource(mContext, R.drawable.albutton, LengthManager.getSolutionButtonSize(), LengthManager.getSolutionButtonSize()));
            Utils.setViewBackground(button, bitmapDrawable);
        }
    }

    void animateFromAlphabetToSolutionOrNot(int alphabetId, int solutionId, Animator.AnimatorListener listener, boolean direction) {
        {
            LetterButtonDrawable drawable = (LetterButtonDrawable) flyingButton.getDrawable();
            drawable.setLabel(alphabet[alphabetId]);
        }

        int[] flyingButtonLocation = new int[2];
        flyingButton.getLocationOnScreen(flyingButtonLocation);

        int[] alphabetLocation = new int[2];
        alphabetButtons[alphabetId].getLocationOnScreen(alphabetLocation);

        int[] solutionLocation = new int[2];
        solutionButtons[solutionId].getLocationOnScreen(solutionLocation);

        {
            int top = getActivity().findViewById(R.id.fragment_container).getTop();
            alphabetLocation[1] -= top;
            solutionLocation[1] -= top;
        }

        for (int i = 0; i < 2; i++) {
            solutionLocation[i] -= LengthManager.getSolutionButtonSize() / 4;
        }

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateInterpolator());

        if (listener != null)
            set.addListener(listener);

        float scale = LengthManager.getSolutionButtonSize() / (float) LengthManager.getAlphabetButtonSize();

        set.playTogether(
                ObjectAnimator.ofFloat(flyingButton, "translationX", direction? alphabetLocation[0]: solutionLocation[0], direction? solutionLocation[0]: alphabetLocation[0]),
                ObjectAnimator.ofFloat(flyingButton, "translationY", direction? alphabetLocation[1]: solutionLocation[1], direction? solutionLocation[1]: alphabetLocation[1]),
                ObjectAnimator.ofFloat(flyingButton, "scaleX", direction? 1: scale, direction? scale: 1),
                ObjectAnimator.ofFloat(flyingButton, "scaleY", direction? 1: scale, direction? scale: 1)
        );

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                flyingButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                flyingButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        set.setDuration(200).start();
    }


    public void alphabetClicked(final int id) {
        if (alphabetGone[id] != Level.AlphabetState.IN_THERE)
            return;

        int firstPlace = -1;
        for (int i = 0; i < solutionButtons.length; i++) {
            View child = solutionButtons[i];
            if (child == null)
                continue;
            if (placeHolder[i] == -1) {
                firstPlace = i;
                break;
            }
        }
        if (firstPlace == -1)
            return;

        placeHolder[firstPlace] = id;
        alphabetGone[id] = Level.AlphabetState.CLICKED;

        final int finalFirstPlace = firstPlace;
        animateFromAlphabetToSolutionOrNot(id, firstPlace, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                updateAlphabet(id);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                updateSolution(finalFirstPlace);
                checkLevelCompleted(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }, true);
    }

    private void checkLevelCompleted(boolean b) {
        for (int i = 0; i < solution.length; i++) {
            if (solution[i].equals(" ") || solution[i].equals("."))
                continue;
            if (placeHolder[i] == -1)
                return;
            String text = alphabet[placeHolder[i]];
            if (!solution[i].equals(text))
                return;
        }

        mLevel.clearSolution(preferences);

        addLevelFinishedLayout();

        // TODO: look up this
        /*if (giveHimPrize && !levelData.isSolved(preferences)) {
            CoinManager.earnCoins(CoinManager.LEVEL_COMPELETED_PRIZE, preferences);
        } else {
            View coinFrame = findViewById(R.id.coinFrame);
            coinFrame.setVisibility(View.GONE);
        }*/

        MediaPlayer.create(getActivity(), R.raw.sound_correct).start();

        mLevel.yeahHeSolvedIt();
    }

    private void customizeTextView(TextView textView, String label, float textSize) {
        textView.setText(label);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getStoreItemFontSize());
        textView.setTextColor(Color.WHITE);
        textView.setShadowLayer(1, 2, 2, Color.BLACK);
        textView.setTypeface(FontsHolder.getHoma(textView.getContext()));
    }


    private void addLevelFinishedLayout() {
        greetingsView = (LinearLayout) inflater.inflate(R.layout.view_level_finished, null);

        ((IntroActivity) getActivity()).pushToViewStack(greetingsView, true);

        {
            View container = greetingsView.findViewById(R.id.container);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
            layoutParams.leftMargin = layoutParams.rightMargin = LengthManager.getStoreDialogMargin();
        }

        {
            View contents = greetingsView.findViewById(R.id.contents);

            DialogDrawable drawable = new DialogDrawable(mContext);
            drawable.setTopPadding(LengthManager.getLevelFinishedDialogTopPadding());
            Utils.setViewBackground(contents, drawable);

            int padding = LengthManager.getLevelFinishedDialogPadding();
            contents.setPadding(padding, 0, padding, padding);
        }

        {
            TextView prize = (TextView) greetingsView.findViewById(R.id.prize);
            if (mLevel.getCurrentPrize() > 0) {
                String prizeString = "+" + Utils.numeralStringToPersianDigits("" + mLevel.getCurrentPrize());
                customizeTextView(prize, prizeString, LengthManager.getLevelAuthorTextSize());

                Utils.resizeView(prize, LengthManager.getPrizeBoxSize(), LengthManager.getPrizeBoxSize());
                Utils.setViewBackground(prize, new BitmapDrawable(getResources(), ImageManager.loadImageFromResource(getActivity(), R.drawable.coin, LengthManager.getPrizeBoxSize(), LengthManager.getPrizeBoxSize())));

                ObjectAnimator.ofFloat(prize, "rotation", 0, 315).setDuration(0).start();
            } else {
                prize.setVisibility(View.GONE);
            }
        }

        {
            ImageView tickView = (ImageView) greetingsView.findViewById(R.id.tickView);
            ((ViewGroup.MarginLayoutParams) tickView.getLayoutParams()).rightMargin = (int) (0.125 * LengthManager.getTickViewSize());
            tickView.setImageBitmap(ImageManager.loadImageFromResource(mContext, R.drawable.correct, LengthManager.getTickViewSize(), LengthManager.getTickViewSize()));
            Utils.resizeView(tickView, LengthManager.getTickViewSize(), LengthManager.getTickViewSize());
        }

        {
            final ImageView nextButton = (ImageView) greetingsView.findViewById(R.id.next_level_button);
            ImageView homeButton = (ImageView) greetingsView.findViewById(R.id.home_button);


            if (mLevel.getId() + 1 < mLevel.getWrapperPackage().getNumberOfLevels()) {
                nextButton.setImageBitmap(ImageManager.loadImageFromResource(mContext, R.drawable.next_button, LengthManager.getLevelFinishedButtonsSize(), LengthManager.getLevelFinishedButtonsSize()));
                Utils.resizeView(nextButton, LengthManager.getLevelFinishedButtonsSize(), LengthManager.getLevelFinishedButtonsSize());

                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoadingManager.startTask(new TaskStartedListener() {
                            @Override
                            public void taskStarted() {
                                ((IntroActivity) getActivity()).popFromViewStack(greetingsView);

                                Level level = mLevel.getWrapperPackage().getLevel(mLevel.getId() + 1);
                                LevelFragment newFragment = LevelFragment.newInstance(level);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.popBackStack();

                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.remove(LevelFragment.this);
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            }
                        });
                    }
                });
            } else {
                nextButton.setVisibility(View.GONE);
                greetingsView.findViewById(R.id.separatorSpace).setVisibility(View.GONE);
            }

            homeButton.setImageBitmap(ImageManager.loadImageFromResource(mContext, R.drawable.home_button, LengthManager.getLevelFinishedButtonsSize(), LengthManager.getLevelFinishedButtonsSize()));
            Utils.resizeView(homeButton, LengthManager.getLevelFinishedButtonsSize(), LengthManager.getLevelFinishedButtonsSize());

            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoadingManager.startTask(new TaskStartedListener() {
                        @Override
                        public void taskStarted() {
                            ((IntroActivity) getActivity()).popFromViewStack(greetingsView);
                            getActivity().onBackPressed();
                            LoadingManager.endTask();
                        }
                    });
                }
            });
        }

        {
            TextView levelSolution = (TextView) greetingsView.findViewById(R.id.level_solution);
            TextView levelAuthor = (TextView) greetingsView.findViewById(R.id.author);

            customizeTextView(levelSolution, mLevel.getSolution(), LengthManager.getLevelSolutionTextSize());
            customizeTextView(levelAuthor, mLevel.getAuthor(), LengthManager.getLevelAuthorTextSize());
        }
    }

    public void solutionClicked(final int id) {
        final int placeHolderId = placeHolder[id];

        if (placeHolderId == -1)
            return;

        if (alphabetGone[placeHolderId] == Level.AlphabetState.FIXED)
            return;

        placeHolder[id] = -1;
        alphabetGone[placeHolderId] = Level.AlphabetState.IN_THERE;

        animateFromAlphabetToSolutionOrNot(placeHolderId, id, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                updateSolution(id);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                updateAlphabet(placeHolderId);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }, false);

    }



    private void setUpSolutionLinearLayout(LayoutInflater inflater, View view) {
        final float solutionButtonSize = LengthManager.getSolutionButtonSize();
        final float gapSize = solutionButtonSize / 2;
        final LinearLayout linesLayout = (LinearLayout) view.findViewById(R.id.solution_rows);
        linesLayout.removeAllViewsInLayout();

        LinearLayout currentRow = null;

        for (int i = 0; i < solution.length; i++) {
            if (i == 0 || solution[i].equals(".")) {
                if (currentRow != null) {
                    currentRow.addView(Utils.makeNewSpace(mContext));
                    Utils.reverseLinearLayout(currentRow);
                    linesLayout.addView(currentRow);
                }
                currentRow = new LinearLayout(mContext);
                currentRow.addView(Utils.makeNewSpace(mContext));
                if (solution[i].equals("."))
                    continue;
            }

            if (solution[i].equals(" ")) {
                View space = new View(mContext);
                space.setLayoutParams(new LinearLayout.LayoutParams((int) gapSize, ViewGroup.LayoutParams.MATCH_PARENT));
                currentRow.addView(space);
                continue;
            }

            ImageView button = new ImageView(getActivity());
            button.setImageDrawable(new LetterButtonDrawable(solution[i], getActivity()));
            button.setLayoutParams(new LinearLayout.LayoutParams((int) solutionButtonSize, (int) solutionButtonSize));

            final int id = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    solutionClicked(id);
                    mLevel.save(alphabetGone, placeHolder);
                }
            });

            solutionButtons[i] = button;
            currentRow.addView(button);

            updateSolution(i);
        }

        if (currentRow != null) {
            currentRow.addView(Utils.makeNewSpace(mContext));
            Utils.reverseLinearLayout(currentRow);
            linesLayout.addView(currentRow);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LoadingManager.endTask();
    }

    @Override
    public void onPause() {
        super.onPause();

        for (int i = 0; i < resources.length; i++)
            getActivity().deleteFile("mm_" + i);
    }

    @Override
    public void onStop() {
        super.onDestroy();

        getActivity().findViewById(R.id.logo).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.cheat_button).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.cheat_button).setOnClickListener(null);
    }

    public Multimedia[] getMultimedia() {
        return resources;
    }

    public Level getLevel() {
        return mLevel;
    }

    private class NotEnoughMoneyException extends Exception {
    }

    private class ImpossibleCheatException extends Exception {
    }
}

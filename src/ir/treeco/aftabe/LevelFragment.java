package ir.treeco.aftabe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private FrameLayout[] alphabetButtons;
    private String[] solution;
    private FrameLayout[] solutionButtons;
    private SharedPreferences preferences;
    private Level.AlphabetState[] alphabetGone;
    private int[] placeHolder;
    private Context mContext;
    Typeface buttonFont;
    private FrameLayout flyingButton;
    private View[] cheatButtons;
    private View blackWidow;
    private Multimedia[] resources;

    public static LevelFragment newInstance(Level mLevel) {
        LevelFragment levelFragment = new LevelFragment();
        levelFragment.mLevel = mLevel;
        return levelFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().findViewById(R.id.logo).setVisibility(View.INVISIBLE);

        final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_level, container, false);

        mContext = container.getContext();
        preferences = container.getContext().getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        buttonFont = FontsHolder.getTabBarFont(mContext);

        alphabet = mLevel.getAlphabetLabels();
        alphabetButtons = new FrameLayout[alphabet.length];
        solution = mLevel.getSolutionLabels();
        solutionButtons = new FrameLayout[solution.length];

        try {
            alphabetGone = mLevel.getAlphabetGone(preferences);
            placeHolder = mLevel.getPlaceHolder(preferences);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        loadResources();

        //setUpTitleBar();
        setUpFlyingButton(layout);
        setUpSolutionLinearLayout(inflater, layout);
        setUpAlphabetLinearLayout(inflater, layout);
        setUpImagePlace(layout);
        setUpCheatLayout(layout);
        setupCheatButton(layout);

        return layout;
    }

    private void loadResources() {
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
    }

    private void setupCheatButton(View layout) {
        final Bitmap cheatBitmap = ImageManager.loadImageFromResource(layout.getContext(), R.drawable.cheat_button, LengthManager.getCheatButtonSize(), LengthManager.getCheatButtonSize());
        final Bitmap backBitmap = ImageManager.loadImageFromResource(layout.getContext(), R.drawable.back_button, LengthManager.getCheatButtonSize(), LengthManager.getCheatButtonSize());

        final ImageView cheatButton = (ImageView) getActivity().findViewById(R.id.cheat_button);
        cheatButton.setVisibility(View.VISIBLE);
        cheatButton.setOnClickListener(new View.OnClickListener() {
            boolean on = false;

            @Override
            public void onClick(View _view) {
                if (!on) {
                    cheatButton.setImageBitmap(backBitmap);
                    showCheats();
                } else {
                    cheatButton.setImageBitmap(cheatBitmap);
                    hideCheats();
                }
                on = !on;
            }
        });

        cheatButton.setImageBitmap(cheatBitmap);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cheatButton.getLayoutParams();
        layoutParams.leftMargin = (int) (0.7 * LengthManager.getScreenWidth());
        layoutParams.topMargin = (int) (0.03 * LengthManager.getScreenWidth());
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
                    //doCheat(finalI);
                }
            });
        }
    }

    void makeCheatFailedToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    void doCheat(int id) {
        try {
            if (id == 0) cheatAndRemoveSomeLetters();
            if (id == 1) cheatAndRevealALetter();
            if (id == 2) cheatAndSkipLevel();
            //getActivity().onBackPressed();
        } catch (NotEnoughMoneyException e) {
            Log.e("COINS", "" + CoinManager.getCoinsCount(preferences));
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
        if (!mLevel.isSolved(preferences) && CoinManager.getCoinsCount(preferences) < CoinManager.SKIP_LEVEL_COST)
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

        if (!mLevel.isSolved(preferences)) {
            CoinManager.spendCoins(CoinManager.SKIP_LEVEL_COST, preferences);
        }

        mLevel.save(alphabetGone, placeHolder, preferences);
    }
    public void cheatAndRemoveSomeLetters() throws JSONException, NotEnoughMoneyException, ImpossibleCheatException {
        if (!mLevel.isSolved(preferences) && CoinManager.getCoinsCount(preferences) < CoinManager.ALPHABET_HIDING_COST)
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

            assert neededCount <= remainingCount;

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

        if (!mLevel.isSolved(preferences)) {
            CoinManager.spendCoins(CoinManager.ALPHABET_HIDING_COST, preferences);
        }

        mLevel.save(alphabetGone, placeHolder, preferences);
    }
    public void cheatAndRevealALetter() throws JSONException, NotEnoughMoneyException, ImpossibleCheatException {
        if (!mLevel.isSolved(preferences) && CoinManager.getCoinsCount(preferences) < CoinManager.LETTER_REVEAL_COST)
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

        assert place != -1;

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

        if (!mLevel.isSolved(preferences)) {
            CoinManager.spendCoins(CoinManager.LETTER_REVEAL_COST, preferences);
        }

        mLevel.save(alphabetGone, placeHolder, preferences);
    }


    private void setUpFlyingButton(View view) {
        FrameLayout button = (FrameLayout) ((FrameLayout) view).getChildAt(1);
        button.setVisibility(View.INVISIBLE);
        ((ImageView) button.getChildAt(0)).setImageBitmap(ImageManager.loadImageFromResource(mContext, R.drawable.albutton, LengthManager.getAlphabetButtonSize(), LengthManager.getAlphabetButtonSize()));
        ((TextView) button.getChildAt(1)).setTypeface(FontsHolder.getTabBarFont(view.getContext()));
        ((TextView) button.getChildAt(1)).setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getAlphabetFontSize());
        button.setLayoutParams(new FrameLayout.LayoutParams(LengthManager.getAlphabetButtonSize(), LengthManager.getAlphabetButtonSize()));
        this.flyingButton = button;
    }

    private void setUpImagePlace(final View view) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.multimedia);
        viewPager.setAdapter(new MultimediaAdapter(this));
        //ImageView levelImageView = (ImageView) view.findViewById(R.id.image);

        //levelImageView.setImageBitmap(ImageManager.loadImageFromInputStream(mLevel.getThumbnail(), LengthManager.getLevelImageWidth(), LengthManager.getLevelImageHeight()));

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

        for (int i = 0; i < 3; i++)
            Utils.setViewBackground(cheatButtons[i], new CheatDrawable(view.getContext(), i, cheatBack, cheatTitles[i], "۱۲۰"));

        blackWidow = view.findViewById(R.id.black_widow);
    }

    public void showCheats() {
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

        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(600).start();
    }

    public void hideCheats() {

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

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                for (View view: cheatButtons)
                    view.setVisibility(View.INVISIBLE);
                blackWidow.setVisibility(View.INVISIBLE);
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

                FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.button_alphabet, null);
                TextView textView = (TextView) frameLayout.findViewById(R.id.letter);

                textView.setFocusable(false);
                textView.setFocusableInTouchMode(false);
                textView.setClickable(false);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getAlphabetFontSize());

                textView.setTypeface(buttonFont);

                frameLayout.setLayoutParams(new LinearLayout.LayoutParams(alphabetButtonSize, alphabetButtonSize));

                currentRow.addView(frameLayout);

                frameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLevel.save(alphabetGone, placeHolder, preferences);
                        alphabetClicked(id);
                    }
                });

                alphabetButtons[id] = frameLayout;

                updateAlphabet(id);
            }
            currentRow.addView(Utils.makeNewSpace(mContext));
            linesLayout.addView(currentRow);
        }

        Log.d("aftabe_plus", "linesLayout: " + linesLayout.getChildCount());

    }

    public void updateAlphabet(int id) {
        FrameLayout frameLayout = alphabetButtons[id];
        ImageView imageView = (ImageView) frameLayout.findViewById(R.id.background);
        TextView textView = (TextView) frameLayout.findViewById(R.id.letter);

        if (alphabetGone[id] != Level.AlphabetState.IN_THERE) {
            imageView.setImageDrawable(null);
            textView.setText(null);
        } else {
            imageView.setImageBitmap(ImageManager.loadImageFromResource(mContext, R.drawable.albutton, LengthManager.getAlphabetButtonSize(), LengthManager.getAlphabetButtonSize()));
            textView.setText(alphabet[id]);
        }
    }

    public void updateSolution(int id) {
        FrameLayout frameLayout = solutionButtons[id];
        ImageView imageView = (ImageView) frameLayout.findViewById(R.id.background);
        TextView textView = (TextView) frameLayout.findViewById(R.id.letter);
        if (placeHolder[id] == -1) {
            imageView.setImageBitmap(ImageManager.loadImageFromResource(mContext, R.drawable.place_holder, LengthManager.getSolutionButtonSize(), LengthManager.getSolutionButtonSize()));
            textView.setText(null);
        } else {
            imageView.setImageBitmap(ImageManager.loadImageFromResource(mContext, R.drawable.albutton, LengthManager.getSolutionButtonSize(), LengthManager.getSolutionButtonSize()));
            textView.setText(alphabet[placeHolder[id]]);
            textView.setTextColor(alphabetGone[placeHolder[id]] == Level.AlphabetState.FIXED ? Color.rgb(0, 180, 0) : Color.rgb(102, 102, 102));
        }
    }

    void animateFromAlphabetToSolutionOrNot(int alphabetId, int solutionId, Animator.AnimatorListener listener, boolean direction) {
        ((TextView) flyingButton.getChildAt(1)).setText(alphabet[alphabetId]);

        /*flyingButton.setTop(0);
        flyingButton.setLeft(0);
        flyingButton.setTranslationX(0);
        flyingButton.setTranslationY(0);*/


        int[] flyingButtonLocation = new int[2];
        flyingButton.getLocationOnScreen(flyingButtonLocation);

        int[] alphabetLocation = new int[2];
        alphabetButtons[alphabetId].getLocationOnScreen(alphabetLocation);

        int[] solutionLocation = new int[2];
        solutionButtons[solutionId].getLocationOnScreen(solutionLocation);

        alphabetLocation[1] -= 180;
        solutionLocation[1] -= 180;


        for (int i = 0; i < 2; i++) {
            //alphabetLocation[i] -= LengthManager.getAlphabetButtonSize() / 4;
            solutionLocation[i] -= LengthManager.getSolutionButtonSize() / 4;
        }

        //Log.d("position of flyer", "" + flyingButtonLocation[0] + "," + flyingButtonLocation[1]);
        //Log.d("alphabetButton", "" + alphabetLocation[0] + "," + alphabetLocation[1]);
        //Log.d("solutionButton", "" + solutionLocation[0] + "," + solutionLocation[1]);

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateInterpolator());

        if (listener != null)
            set.addListener(listener);

        float scale = LengthManager.getSolutionButtonSize() / (float) LengthManager.getAlphabetButtonSize();

        set.playTogether(
                ObjectAnimator.ofFloat(flyingButton, "translationX", direction? alphabetLocation[0]: solutionLocation[0], direction? solutionLocation[0]: alphabetLocation[0]),
                ObjectAnimator.ofFloat(flyingButton, "translationY", direction? alphabetLocation[1]: solutionLocation[1], direction? solutionLocation[1]: alphabetLocation[1]),
                //ObjectAnimator.ofFloat(flyingButton, "pivotX", 50, 50),
                //ObjectAnimator.ofFloat(flyingButton, "pivotY", 50, 50),
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

            FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.button_solution, null);

            frameLayout.setFocusable(false);
            frameLayout.setFocusableInTouchMode(false);
            frameLayout.setClickable(false);

            // ImageView imageView = (ImageView) frameLayout.findViewById(R.id.solutionImageView);
            TextView textView = (TextView) frameLayout.findViewById(R.id.letter);

            textView.setFocusable(false);
            textView.setFocusableInTouchMode(false);
            textView.setClickable(false);

            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getSolutionFontSize());
            textView.setTypeface(buttonFont);

            frameLayout.setLayoutParams(new LinearLayout.LayoutParams((int) solutionButtonSize, (int) solutionButtonSize));

            final int id = i;

            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLevel.save(alphabetGone, placeHolder, preferences);
                    solutionClicked(id);
                }
            });

            solutionButtons[i] = frameLayout;
            currentRow.addView(frameLayout);

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
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.logo).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.cheat_button).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.cheat_button).setOnClickListener(null);
    }

    public Multimedia[] getMultimedia() {
        return resources;
    }

    private class NotEnoughMoneyException extends Exception {
    }

    private class ImpossibleCheatException extends Exception {
    }
}

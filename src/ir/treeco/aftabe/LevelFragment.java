package ir.treeco.aftabe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import ir.treeco.aftabe.packages.Level;
import ir.treeco.aftabe.utils.*;
import org.json.JSONException;

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

    public static LevelFragment newInstance(Level mLevel) {
        LevelFragment levelFragment = new LevelFragment();
        levelFragment.mLevel = mLevel;
        return levelFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_level, container, false);

        mContext = container.getContext();
        preferences = container.getContext().getSharedPreferences("levels", Context.MODE_PRIVATE);
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

        //setUpTitleBar();
        setUpFlyingButton(layout);
        setUpSolutionLinearLayout(inflater, layout);
        setUpAlphabetLinearLayout(inflater, layout);
        setUpImagePlace(layout);
        //setUpCheatLayout();


        return layout;
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

    private void setUpImagePlace(View view) {
        ImageView levelImageView = (ImageView) view.findViewById(R.id.image);
        levelImageView.setImageBitmap(ImageManager.loadImageFromInputStream(mLevel.getImage(), LengthManager.getLevelImageWidth(), LengthManager.getLevelImageHeight()));
        levelImageView.setBackgroundColor(Color.RED);
        Utils.resizeView(levelImageView, LengthManager.getLevelImageWidth(), LengthManager.getLevelImageHeight());

        ImageView frame = (ImageView) view.findViewById(R.id.frame);
        frame.setImageBitmap(ImageManager.loadImageFromResource(view.getContext(), R.drawable.frame, LengthManager.getLevelImageFrameWidth(), LengthManager.getLevelImageFrameHeight()));
        Utils.resizeView(frame, LengthManager.getLevelImageFrameWidth(), LengthManager.getLevelImageFrameHeight());

        Utils.resizeView(view.findViewById(R.id.image_place), ViewGroup.LayoutParams.MATCH_PARENT, LengthManager.getLevelImageFrameHeight());
    }

    private void setUpAlphabetLinearLayout(LayoutInflater inflater, View view) {
        final int alphabetButtonSize = LengthManager.getAlphabetButtonSize();
        final LinearLayout linesLayout = (LinearLayout) view.findViewById(R.id.alphabet_rows);
        linesLayout.removeAllViewsInLayout();


        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            LinearLayout currentRow = new LinearLayout(mContext);
            currentRow.addView(Utils.makeNewSpace(mContext));
            for (int j = 0; j < 7; j++) {
                final int id = i * 7 + j;

                FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.button_alphabet, null);
                //frameLayout.setBackgroundColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
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
            textView.setTextColor(alphabetGone[placeHolder[id]] == Level.AlphabetState.FIXED? Color.rgb(0, 180, 0): Color.rgb(102, 102, 102));
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

}

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.treeco.aftabe.packages.Level;
import ir.treeco.aftabe.utils.FontsHolder;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.Utils;
import org.json.JSONException;

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

    public static LevelFragment newInstance(Level mLevel) {
        LevelFragment levelFragment = new LevelFragment();
        levelFragment.mLevel = mLevel;
        return levelFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_level, container, false);
        ImageView levelImageView = (ImageView) layout.findViewById(R.id.level_image_view);
        levelImageView.setImageBitmap(ImageManager.loadImageFromInputStream(mLevel.getImage(), 100, 100));

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
        setUpSolutionLinearLayout(inflater, layout);
        setUpAlphabetLinearLayout(inflater, layout);
        setUpImagePlace();
        //setUpCheatLayout();


        return layout;
    }

    private void setUpImagePlace() {

    }

    private void setUpAlphabetLinearLayout(LayoutInflater inflater, View view) {
        final int alphabetButtonSize = LengthManager.getAlphabetButtonSize();
        final LinearLayout linesLayout = (LinearLayout) view.findViewById(R.id.alphabet_rows);
        linesLayout.removeAllViewsInLayout();



        for (int i = 0; i < 3; i++) {
            LinearLayout currentRow = new LinearLayout(mContext);
            currentRow.addView(makeNewSpace());
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
            currentRow.addView(makeNewSpace());
            linesLayout.addView(currentRow);
        }

        Log.d("aftabe_plus", "linesLayout: " + linesLayout.getChildCount());

    }

    public void updateAlphabet(int id) {
        FrameLayout frameLayout = alphabetButtons[id];
        ImageView imageView = (ImageView) frameLayout.findViewById(R.id.background);
        TextView textView = (TextView) frameLayout.findViewById(R.id.letter);

        if (alphabetGone[id] != Level.AlphabetState.IN_THERE) {
            imageView.setImageBitmap(ImageManager.loadImageFromResource(mContext, R.drawable.place_holder, LengthManager.getAlphabetButtonSize(), LengthManager.getAlphabetButtonSize()));
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


    public void alphabetClicked(int id) {
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

        updateSolution(firstPlace);
        updateAlphabet(id);

        checkLevelCompleted(true);
    }

    private void checkLevelCompleted(boolean b) {

    }

    public void solutionClicked(int id) {
        int placeHolderId = placeHolder[id];

        if (placeHolderId == -1)
            return;

        if (alphabetGone[placeHolderId] == Level.AlphabetState.FIXED)
            return;

        placeHolder[id] = -1;
        alphabetGone[placeHolderId] = Level.AlphabetState.IN_THERE;

        updateAlphabet(placeHolderId);
        updateSolution(id);
    }


    private View makeNewSpace() {
        View space = new View(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        space.setLayoutParams(layoutParams);
        return space;
    }

    private void setUpSolutionLinearLayout(LayoutInflater inflater, View view) {
        final float solutionButtonSize = LengthManager.getSolutionButtonSize();
        final float gapSize = solutionButtonSize / 2;
        final LinearLayout linesLayout = (LinearLayout) view.findViewById(R.id.solution_rows);
        linesLayout.removeAllViewsInLayout();

        LinearLayout currentRow = null;
        int index = 0;

        for (int i = 0; i < solution.length; i++) {
            if (i == 0 || solution[i].equals(".")) {
                if (currentRow != null) {
                    currentRow.addView(makeNewSpace());
                    Utils.reverseLinearLayout(currentRow);
                    linesLayout.addView(currentRow);
                }
                currentRow = new LinearLayout(mContext);
                currentRow.addView(makeNewSpace());
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
            currentRow.addView(makeNewSpace());
            Utils.reverseLinearLayout(currentRow);
            linesLayout.addView(currentRow);
        }

    }
}

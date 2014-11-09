package ir.treeco.aftabe.packages;

import android.content.SharedPreferences;
import ir.treeco.aftabe.mutlimedia.Multimedia;
import ir.treeco.aftabe.utils.Encryption;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by hossein on 7/31/14.
 */
public class Level {
    private final SharedPreferences preferences;
    private String solution, author;
    private Package wrapperPackage;
    private String thumbnailName;
    private Multimedia[] resources;
    private int prize;
    private int id;

    public String getSolution() {
        return solution;
    }

    public String getAuthor() {
        return author;
    }

    public Package getWrapperPackage() {
        return wrapperPackage;
    }

    public int getId() {
        return id;
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public Multimedia[] getResources() {
        return resources;
    }

    public void setResources(Multimedia[] resources) {
        this.resources = resources;
    }

    public Level(SharedPreferences preferences, String author, String solution, String thumbnailName, Package wPackage, int id, int prize) {
        this.author = author;
        this.preferences = preferences;
        this.solution = Encryption.decryptBase64(solution);
        this.wrapperPackage = wPackage;
        this.id = id;
        this.thumbnailName = thumbnailName;
        this.prize = prize;
    }

    public void clearSolution(SharedPreferences preferences) {
        AlphabetState[] alphabetGone;
        int[] placeHolder;

        try {
            alphabetGone = getAlphabetGone(preferences);
            placeHolder = getPlaceHolder(preferences);
        } catch (JSONException e) {
            return;
        }

        for (int i = 0; i < placeHolder.length; i++)
            placeHolder[i] = -1;
        for (int i = 0; i < alphabetGone.length; i++)
            alphabetGone[i] = AlphabetState.IN_THERE;

        save(alphabetGone, placeHolder);
    }

    public static enum AlphabetState {
        IN_THERE,
        CLICKED,
        REMOVED,
        FIXED
    }

    public static final int BUTTON_COUNT = 21;

    public String[] getAlphabetLabels() {
        String alphabet = "آابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهی";
        Random random = new Random(id);
        String[] labels = new String[BUTTON_COUNT];

        int position = 0;
        for (int i = 0; i < solution.length(); i++)
            if (solution.charAt(i) != ' ' && solution.charAt(i) != '.')
                labels[position++] = "" + solution.charAt(i);

        while (position < BUTTON_COUNT)
            labels[position++] = "" + alphabet.charAt(random.nextInt(alphabet.length()));

        for (int i = 1; i < BUTTON_COUNT; i++) {
            int j = random.nextInt(i);
            String tmp;
            tmp = labels[i];
            labels[i] = labels[j];
            labels[j] = tmp;
        }

        return labels;
    }

    public AlphabetState[] getAlphabetGone(final SharedPreferences preferences) throws JSONException {
        final String alphabetJSONString = preferences.getString(alphabetGoneTag(), null);

        if (alphabetJSONString == null) {
            final AlphabetState alphabetGone[] = new AlphabetState[BUTTON_COUNT];
            for (int i = 0; i < alphabetGone.length; i++)
                alphabetGone[i] = AlphabetState.IN_THERE;
            return alphabetGone;
        }

        final JSONArray array = new JSONArray(alphabetJSONString);

        final AlphabetState[] alphabetGone = new AlphabetState[array.length()];
        for (int i = 0; i < array.length(); i++)
            alphabetGone[i] = AlphabetState.valueOf(array.getString(i));

        serviceAlphabetGone(alphabetGone);

        return alphabetGone;
    }

    public String[] getSolutionLabels() {
        String labels[] = new String[solution.length()];
        for (int i = 0; i < solution.length(); i++)
            labels[i] = "" + solution.charAt(i);
        return labels;
    }

    private String alphabetGoneTag() {
        return "package_" + getWrapperPackage().meta.getId() + "_level_" + id + "_alphabet_gone";
    }

    private String placeHolderTag() {
        return "package_" + getWrapperPackage().meta.getId() + "_level_" + id + "_place_holder";
    }

    public void save(AlphabetState[] alphabetGone, int[] placeHolder) {
        JSONArray alphabetArray = new JSONArray();
        for (AlphabetState b: alphabetGone)
            alphabetArray.put(b.toString());

        JSONArray placeHolderArray = new JSONArray();
        for (int i: placeHolder)
            placeHolderArray.put(i);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(alphabetGoneTag(), alphabetArray.toString());
        editor.putString(placeHolderTag(), placeHolderArray.toString());
        editor.commit();
    }

    private void serviceAlphabetGone(AlphabetState[] alphabetGone) {
        if (!isSolved())
            return;
        for (int i = 0; i < alphabetGone.length; i++)
            if (alphabetGone[i] == AlphabetState.REMOVED)
                alphabetGone[i] = AlphabetState.IN_THERE;
            else if (alphabetGone[i] == AlphabetState.FIXED)
                alphabetGone[i] = AlphabetState.CLICKED;
    }

    public int[] getPlaceHolder(final SharedPreferences preferences) throws JSONException {
        final String placeHolderJSONString = preferences.getString(placeHolderTag(), null);

        if (placeHolderJSONString == null) {
            final int placeHolder[] = new int[solution.length()];
            Arrays.fill(placeHolder, -1);
            return placeHolder;
        }

        final JSONArray array = new JSONArray(placeHolderJSONString);

        final int[] placeHolder = new int[array.length()];
        for (int i = 0; i < array.length(); i++)
            placeHolder[i] = array.getInt(i);

        return placeHolder;
    }

    public boolean isSolved() {
        return preferences.getBoolean(levelSolvedTag(), false);
    }

    public void yeahHeSolvedIt() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(levelSolvedTag(), true);
        editor.commit();
    }

    private String levelSolvedTag() {
        return "package_" + getWrapperPackage().meta.getId() + "_level_" + id + "_is_solved";
    }

    public boolean isLocked() {
        return id != 0 && !isSolved() && !getWrapperPackage().getLevel(id - 1).isSolved();
    }


    private String resourceTag(int resourceId) {
        return "package_" + getWrapperPackage().meta.getId() + "_level_" + id + "_resource_" + resourceId + "is_unlocked";
    }

    public boolean isResourceUnlocked(int resourceId) {
        return resourceId == 0 || isSolved() || preferences.getBoolean(resourceTag(resourceId), false);
    }

    public void unlockResource(int resourceId) {
        if (isResourceUnlocked(resourceId))
            return;

        int currentPrizeChange = preferences.getInt(levelPrizeChangeTag(), 0);
        currentPrizeChange -= resources[resourceId].getCost();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(resourceTag(resourceId), true);
        editor.putInt(levelPrizeChangeTag(), currentPrizeChange);
        editor.commit();
    }

    private String levelPrizeChangeTag() {
        return "package_" + getWrapperPackage().meta.getId() + "_level_" + id + "_prize_change";
    }

    public int getCurrentPrize() {
        return prize + preferences.getInt(levelPrizeChangeTag(), 0);
    }
}

package ir.treeco.aftabe.packages;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import ir.treeco.aftabe.utils.Encryption;
import ir.treeco.aftabe.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by hossein on 7/31/14.
 */
public class Level {
    private String solution, author;
    private Package wrapperPackage;
    private InputStream Image;
    private int id;
    private Context context;

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

    public void setImage(InputStream inputStream) {
        Image = inputStream;
    }
    public InputStream getImage() {
//        if(wrapperPackage.getState() == PackageState.builtIn) {
//            ZipInputStream zipInputStream = new ZipInputStream(Utils.getInputStreamFromRaw(context,wrapperPackage.getName()
//                            ,"zip"));
//            for(ZipEntry e; (e=zipInputStream.getNextEntry())!=null ; ) {
//                if(e.getName().equals(id+".jpg"))
//                    break;
//            }
//            return zipInputStream;
//        }
//        else {
//            ZipEntry entry = this.wrapperPackage.getData().getEntry(id+".jpg");
//            return this.wrapperPackage.getData().getInputStream(entry);
//        }
        return Image;
    }
//    public InputStream getImage() {
//        if(wrapperPackage.getState() == PackageState.builtIn) {
//            ZipInputStream zipInputStream = new ZipInputStream(Utils.getInputStreamFromRaw(context,wrapperPackage.getName()
//                            ,"zip"));
//            long startTime = System.currentTimeMillis();
//            try {
//                for(ZipEntry e; (e=zipInputStream.getNextEntry())!=null ; ) {
//                    if(e.getName().equals(id+".jpg"))
//                        break;
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            Log.e("aftabe_plus", "iteration took: " + (System.currentTimeMillis() - startTime));
//            return zipInputStream;
//        }
//        else {
//            ZipEntry entry = this.wrapperPackage.getData().getEntry(id+".jpg");
//            try {
//                return this.wrapperPackage.getData().getInputStream(entry);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

    public Level(Context context, String author, String solution, Package wPackage, int id) {
        this.context = context;
        this.author = author;
        this.solution = Encryption.decrypt(solution);
        this.wrapperPackage = wPackage;
        this.id = id;
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

        save(alphabetGone, placeHolder, preferences);
    }

    public static enum AlphabetState {
        IN_THERE,
        CLICKED,
        REMOVED,
        AlphabetState, FIXED
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

        serviceAlphabetGone(alphabetGone, preferences);

        /*String log = "alphabetGone loaded:";
        for (AlphabetState state: alphabetGone)
            log += " " + state;
        Log.i("GOLVAZHE", log);*/

        return alphabetGone;
    }

    public String[] getSolutionLabels() {
        String labels[] = new String[solution.length()];
        for (int i = 0; i < solution.length(); i++)
            labels[i] = "" + solution.charAt(i);
        return labels;
    }

    private String alphabetGoneTag() {
        return "package_" + getWrapperPackage().getId() + "level_" + id + "_alphabet_gone";
    }

    private String placeHolderTag() {
        return "package_" + getWrapperPackage().getId() + "level_" + id + "_place_holder";
    }

    public void save(AlphabetState[] alphabetGone, int[] placeHolder, SharedPreferences preferences) {
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

    private void serviceAlphabetGone(AlphabetState[] alphabetGone, SharedPreferences preferences) {
        if (!isSolved(preferences))
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

    /*public void createAndReplaceThumbnail(Activity activity, int columnWidth, ImageView imageView) {
        ThumbnailCreator createThumbnail =  new ThumbnailCreator(activity, columnWidth, this, imageView);
        new Thread(createThumbnail).start();
    }*/

    public boolean isSolved(SharedPreferences preferences) {
        //return true;
        return preferences.getBoolean(levelSolvedTag(), false);
    }

    public void yeahHeSolvedIt(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(levelSolvedTag(), true);
        editor.commit();
    }

    private String levelSolvedTag() {
        return "package_" + getWrapperPackage().getId() + "level_" + id + "_is_solved";
    }

    public boolean isLocked(SharedPreferences preferences) {
        return id != 0 && !isSolved(preferences) && !getWrapperPackage().getLevel(id - 1).isSolved(preferences);
    }

}

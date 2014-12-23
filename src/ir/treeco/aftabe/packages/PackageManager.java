package ir.treeco.aftabe.packages;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.FrameLayout;
import ir.treeco.aftabe.IntroActivity;
import ir.treeco.aftabe.PackageListAdapter;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.utils.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class PackageManager {
    private final static String TAG = "PackagerManager";

    private final static String PKG_NAME_KEY = "Name";
    private final static String DATA_URL_KEY = "Data URL";
    private final static String COST_KEY = "Cost";
    private final static String DATA_VERSION_KEY = "Data Version";
    private final static String RATE_KEY = "Rate";
    private final static String COLOR_KEY = "Color";
    private final static String HSV_THUMBNAIL_KEY = "HSV Thumbnail";
    private final static String HSV_CHEAT_BUTTON_KEY = "HSV Cheat Button";
    private final static String HSV_BACKGROUND_KEY = "HSV Background";

    private final SharedPreferences preferences;
    IntroActivity activity;
    Context context;

    private MetaPackage[] packages;
    private MetaPackage[] newPackages, localPackages, hotPackages;

    private PackageListAdapter adapter;
    private FrameLayout frameLayout;

    public void setActivity(IntroActivity activity) {
        this.activity = activity;
    }

    public void setAdapter(PackageListAdapter adapter) {
        this.adapter = adapter;
    }

    public PackageManager(Context context, FrameLayout frameLayout) {
        this.context = context;
        this.preferences = context.getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        this.frameLayout = frameLayout;
        //TODO uncomment below
//        refresh();
    }

    public void refresh() {
        Yaml yaml = new Yaml();

        Log.i(TAG, "Refreshing...");


        MetaPackage[] inPackages;

        {
            Log.i(TAG, "Loading built-in packages");

            InputStream inputStream = context.getResources().openRawResource(R.raw.header);

            List<HashMap<String, Object>> headerInfo = (List<HashMap<String, Object>>) yaml.load(inputStream);
            Log.i(TAG, "Got the header for built-ins");

            inPackages = new MetaPackage[headerInfo.size()];

            int index = 0;
            for (HashMap<String, Object> packageInfo : headerInfo) {
                String name = (String) packageInfo.get(PKG_NAME_KEY);

                int[] colors;
                {
                    List<String> colorList = (List<String>) packageInfo.get(COLOR_KEY);
                    colors = new int[colorList.size()];

                    ArrayList<String> tmpColors = new ArrayList<String>();

                    for (String color : colorList)
                        tmpColors.add(color);

                    for (int i = 0; i < tmpColors.size(); i++)
                        colors[i] = Color.parseColor(tmpColors.get(i));
                }

                float[] thumbnailHSV;
                {
                    List<Float> thumbnailHSVList = (List<Float>) packageInfo.get(HSV_THUMBNAIL_KEY);
                    thumbnailHSV = Utils.floatListToArray(thumbnailHSVList);
                }

                float[] cheatButtonHSV;
                {
                    List<Float> cheatButtonHSVList = (List<Float>) packageInfo.get(HSV_CHEAT_BUTTON_KEY);
                    cheatButtonHSV = Utils.floatListToArray(cheatButtonHSVList);
                }

                inPackages[index] = new MetaPackage(context, preferences, colors, thumbnailHSV, cheatButtonHSV, name, index, PackageState.LOCAL, this, 1000);

                // Copy data to internal memory

                File dataFile = new File(context.getFilesDir(), name + ".zip");
                File backThumb = new File(context.getFilesDir(), name + "_back.png");
                File frontThumb = new File(context.getFilesDir(), name + "_front.png");

                Log.i(TAG, "Copying data to internal memory");

                if (!dataFile.exists()) {
                    copyFromRawToInternal(name, "zip");
                    Log.i(TAG, "Copied .zip file");
                }

                if (!backThumb.exists()) {
                    copyFromRawToInternal(name + "_back", "png");
                    Log.i(TAG, "Copied _back.png file");
                }

                if (!frontThumb.exists()) {
                    copyFromRawToInternal(name + "_front", "png");
                    Log.i(TAG, "Copied _front.png file");
                }

                index++;
            }
        }

        MetaPackage[] outPackages = null;
        {
            Log.i(TAG, "Loading remote packages");

            List<HashMap<String, Object>> headerInfo;

            try {
                headerInfo = (List<HashMap<String, Object>>) yaml.load(context.openFileInput("header.yml"));
            } catch (FileNotFoundException e) {
                headerInfo = null;
            }

            if (headerInfo != null) {
                Log.i(TAG, "Got the header for remotes");

                outPackages = new MetaPackage[headerInfo.size()];

                int index = 0;
                for (HashMap<String, Object> packageInfo : headerInfo) {
                    String name = (String) packageInfo.get(PKG_NAME_KEY);

                    int[] colors;
                    {
                        List<String> colorList = (List<String>) packageInfo.get(COLOR_KEY);
                        colors = new int[colorList.size()];

                        ArrayList<String> tmpColors = new ArrayList<String>();

                        for (String color : colorList)
                            tmpColors.add(color);

                        for (int i = 0; i < tmpColors.size(); i++)
                            colors[i] = Color.parseColor(tmpColors.get(i));
                    }

                    int cost = (Integer) packageInfo.get(COST_KEY);
                    int version = (Integer) packageInfo.get(DATA_VERSION_KEY);
                    int rate = (Integer) packageInfo.get(RATE_KEY);
                    String dataUrl = (String) packageInfo.get(DATA_URL_KEY);

                    Log.d(TAG, "Loading package: " + name + " " + cost + " " + version + " " + dataUrl);

                    File dataFile = new File(context.getFilesDir(), name + ".zip");
                    PackageState state = dataFile.exists() ? PackageState.LOCAL : PackageState.REMOTE;

                    if (state == PackageState.LOCAL) {
                        int currentVersion = preferences.getInt(name + "_DATA_VERSION", -1);
                        if (currentVersion < version)
                            state = PackageState.REMOTE;
                    }

                    float[] backgroundHSV;
                    {
                        List<Float> bgHSVList = (List<Float>) packageInfo.get(HSV_BACKGROUND_KEY);
                        backgroundHSV = Utils.floatListToArray(bgHSVList);
                    }

                    float[] cheatButtonHSV;
                    {
                        List<Float> cbHSVList = (List<Float>) packageInfo.get(HSV_CHEAT_BUTTON_KEY);
                        cheatButtonHSV = Utils.floatListToArray(cbHSVList);
                    }

                    outPackages[index] = new MetaPackage(context, preferences, colors, backgroundHSV, cheatButtonHSV, name, index + inPackages.length, state, this, rate);
                    outPackages[index].setCost(cost);
                    outPackages[index].setDataUrl(dataUrl);
                    outPackages[index].setDataVersion(version);

                    index++;
                }
            }
        }

        // Putting things together

        packages = new MetaPackage[inPackages.length + (outPackages != null ? outPackages.length : 0)];

        System.arraycopy(inPackages, 0, packages, 0, inPackages.length);

        if (outPackages != null)
            System.arraycopy(outPackages, 0, packages, inPackages.length, outPackages.length);

        generateAdapterResourceArrays();
    }

    public void generateAdapterResourceArrays() {
        ArrayList<MetaPackage> newPackages = new ArrayList<MetaPackage>();
        ArrayList<MetaPackage> hotPackages = new ArrayList<MetaPackage>();
        ArrayList<MetaPackage> localPackages = new ArrayList<MetaPackage>();

        for (MetaPackage aPackage : packages) {
            if (aPackage.getState() == PackageState.LOCAL)
                localPackages.add(aPackage);

            if (aPackage.getState() == PackageState.REMOTE || aPackage.getState() == PackageState.DOWNLOADING) {
                newPackages.add(aPackage);
                hotPackages.add(aPackage);
            }
        }

        // Convert ArrayList to Array for better performance

        this.newPackages = newPackages.toArray(new MetaPackage[newPackages.size()]);
        this.localPackages = localPackages.toArray(new MetaPackage[localPackages.size()]);
        this.hotPackages = hotPackages.toArray(new MetaPackage[hotPackages.size()]);

        Arrays.sort(this.hotPackages, new Comparator<MetaPackage>() {
            @Override
            public int compare(MetaPackage lhs, MetaPackage rhs) {
                if (lhs.getRate() == rhs.getRate())
                    return 0;
                return lhs.getRate() < rhs.getRate() ? +1 : -1;
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                frameLayout.invalidate();
            }
        });
    }

    public MetaPackage[] getHotPackages() {
        return hotPackages;
    }

    public MetaPackage[] getLocalPackages() {
        return localPackages;
    }

    public MetaPackage[] getNewPackages() {
        return newPackages;
    }

    public void copyFromRawToInternal(String name, String type) {
        InputStream inputStream = Utils.getInputStreamFromRaw(context, name, type);
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(name + "." + type, 0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Utils.pipe(inputStream, fileOutputStream);
            Log.i(TAG, "Piped: " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package ir.treeco.aftabe.packages;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.FrameLayout;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ir.treeco.aftabe.Adapter.PackageListAdapter;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Activity.IntroActivity;
import ir.treeco.aftabe.utils.Utils;

public class PackageManager {
    private final static String TAG = "PackagerManager";

    private final static String KEY_PKG_NAME = "Name";
    private final static String KEY_DATA_URL = "Data URL";
    private final static String KEY_DATA_VERSION = "Data Version";
    private final static String KEY_RATE = "Rate";
    private final static String KEY_COLOR = "Color";
    private final static String KEY_HSV_THUMBNAIL = "HSV Thumbnail";
    private final static String KEY_HSV_CHEAT_BUTTON = "HSV Cheat Button";
    private final static String KEY_HSV_BACKGROUND = "HSV Background";
    private static final String KEY_TOMAN_COST = "TomanCost";
    private static final String KEY_COIN_COST = "CoinCost";
    private static final String KEY_SKU = "SKU";

    private SharedPreferences preferences;
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

    public PackageManager(){

    }

    public void refresh() {
        Yaml yaml = new Yaml();

        Log.i(TAG, "Refreshing...");


        MetaPackage[] inPackages;

        {
            Log.i(TAG, "Loading built-in packages");

            InputStream inputStream = context.getResources().openRawResource(R.raw.local);  //edit by behdad

            List<HashMap<String, Object>> headerInfo = (List<HashMap<String, Object>>) yaml.load(inputStream);
            Log.i(TAG, "Got the header for built-ins");

            inPackages = new MetaPackage[headerInfo.size()];

            int index = 0;
            for (HashMap<String, Object> packageInfo : headerInfo) {
                String name = (String) packageInfo.get(KEY_PKG_NAME);

                int[] colors;
                {
                    List<String> colorList = (List<String>) packageInfo.get(KEY_COLOR);
                    colors = new int[colorList.size()];

                    ArrayList<String> tmpColors = new ArrayList<String>();

                    for (String color : colorList)
                        tmpColors.add(color);

                    for (int i = 0; i < tmpColors.size(); i++)
                        colors[i] = Color.parseColor(tmpColors.get(i));
                }

                float[] thumbnailHSV;
                {
                    List<Float> thumbnailHSVList = (List<Float>) packageInfo.get(KEY_HSV_THUMBNAIL);
                    thumbnailHSV = Utils.floatListToArray(thumbnailHSVList);
                }

                float[] cheatButtonHSV;
                {
                    List<Float> cheatButtonHSVList = (List<Float>) packageInfo.get(KEY_HSV_CHEAT_BUTTON);
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
                    String name = (String) packageInfo.get(KEY_PKG_NAME);

                    int[] colors;
                    {
                        List<String> colorList = (List<String>) packageInfo.get(KEY_COLOR);
                        colors = new int[colorList.size()];

                        ArrayList<String> tmpColors = new ArrayList<String>();

                        for (String color : colorList)
                            tmpColors.add(color);

                        for (int i = 0; i < tmpColors.size(); i++)
                            colors[i] = Color.parseColor(tmpColors.get(i));
                    }

                    int tomanCost = (Integer) packageInfo.get(KEY_TOMAN_COST);
                    int coinCost = (Integer) packageInfo.get(KEY_COIN_COST);
                    int version = (Integer) packageInfo.get(KEY_DATA_VERSION);
                    int rate = (Integer) packageInfo.get(KEY_RATE);
                    String dataUrl = (String) packageInfo.get(KEY_DATA_URL);
                    String sku = (String) packageInfo.get(KEY_SKU);

                    Log.d(TAG, "Loading package: " + name + " " + version + " " + dataUrl + " " + sku);

                    File dataFile = new File(context.getFilesDir(), name + ".zip");
                    PackageState state = dataFile.exists() ? PackageState.LOCAL : PackageState.REMOTE;

                    if (state == PackageState.LOCAL) {
                        int currentVersion = preferences.getInt(name + "_DATA_VERSION", -1);
                        if (currentVersion < version)
                            state = PackageState.REMOTE;
                    }

                    float[] backgroundHSV;
                    {
                        List<Float> bgHSVList = (List<Float>) packageInfo.get(KEY_HSV_BACKGROUND);
                        backgroundHSV = Utils.floatListToArray(bgHSVList);
                    }

                    float[] cheatButtonHSV;
                    {
                        List<Float> cbHSVList = (List<Float>) packageInfo.get(KEY_HSV_CHEAT_BUTTON);
                        cheatButtonHSV = Utils.floatListToArray(cbHSVList);
                    }

                    outPackages[index] = new MetaPackage(context, preferences, colors, backgroundHSV, cheatButtonHSV, name, index + inPackages.length, state, this, rate);
                    outPackages[index].setTomanCost(tomanCost);
                    outPackages[index].setCoinCost(coinCost);
                    outPackages[index].setDataUrl(dataUrl);
                    outPackages[index].setDataVersion(version);
                    outPackages[index].setSku(sku);

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

    public void parsingYml(InputStream ymlFile) {
        List<HashMap<String, Object>> hashMaps = (List<HashMap<String, Object>>) new Yaml().load(ymlFile);
        for (HashMap<String, Object> map : hashMaps) {
            Log.d("armin", map.get("Name").toString());
        }
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

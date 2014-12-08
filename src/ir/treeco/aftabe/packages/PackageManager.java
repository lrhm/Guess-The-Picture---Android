package ir.treeco.aftabe.packages;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.FrameLayout;
import ir.treeco.aftabe.IntroActivity;
import ir.treeco.aftabe.PackageListAdapter;
import ir.treeco.aftabe.PackageListImplicitAdapter;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.utils.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * Created by hossein on 7/31/14.
 */
public class PackageManager {


    private final SharedPreferences preferences;
    private List<HashMap<String, Object>> headerInfo;
    private final static String pkgNameKey= "Name",
                          dataUrlKey = "Data URL",
                          costKey = "Cost",
                          dataVersionKey = "Data Version",
                          rateKey = "Rate";
    Context context;
    private MetaPackage[] packages;
    private MetaPackage[] newPackages, localPackages, hotPackages;
    IntroActivity activity;

    private PackageListAdapter adapter;
    private FrameLayout frameLayout;

    public void setFrameLayout(FrameLayout frameLayout) {
        this.frameLayout = frameLayout;
    }

    public void setActivity(IntroActivity activity) {
        this.activity = activity;
    }

    public void setAdapter(PackageListAdapter adapter) {
        this.adapter = adapter;
    }

    public PackageManager(Context context, FrameLayout frameLayout) {
        this.context = context;
        this.preferences = context.getSharedPreferences(Utils.sharedPrefrencesTag(), Context.MODE_PRIVATE);
        this.frameLayout = frameLayout;
        //TODO uncomment below
//        refresh();
    }

    public void refresh() {
        Log.i("PackageManager","in Refresh");
        MetaPackage[] inPackages=null, outPackages=null;


        //load builtIn Packages
        Log.i("PackageManager","load built in");
        InputStream inputStream = context.getResources().openRawResource(R.raw.header);
        Yaml yaml = new Yaml();
        headerInfo = (List<HashMap<String, Object>>) yaml.load(inputStream);
        Log.i("PackageManager","load built in - got the header");
        inPackages = new MetaPackage[headerInfo.size()];
        int cnt=0;
        for(HashMap<String,Object> pkgInfo : headerInfo) {
            String name = (String) pkgInfo.get(pkgNameKey);
            List<String> colorList = (List<String>) pkgInfo.get("Color");

            Log.e("COLORS", "" + colorList);

            int[] color = new int[colorList.size()];

            ArrayList<String> tmpColors = new ArrayList<String>();

            for (String c: colorList)
                tmpColors.add(c);

            for (int i = 0;i < tmpColors.size(); i++)
                color[i] = Color.parseColor(tmpColors.get(i));

            List<Float> thumbnailHSVList = (List<Float>) pkgInfo.get("HSV Thumbnail");
            float[] thumbnailHSV = Utils.floatListToArray(thumbnailHSVList);
            List<Float> cheatButtonHSVList = (List<Float>) pkgInfo.get("HSV Cheat Button");
            float[] cheatButtonHSV = Utils.floatListToArray(cheatButtonHSVList);
            inPackages[cnt] = new MetaPackage(context, preferences, color, thumbnailHSV, cheatButtonHSV, name, cnt, PackageState.LOCAL, this, 1000);

            //Copy data,Thumbnail to memory
            File dataFile = new File(context.getFilesDir(), name+".zip");
            File backThumb = new File(context.getFilesDir(), name+"_back.png");
            File frontThumb = new File(context.getFilesDir(), name+"_front.png");
            Log.i("PackageManager","transfering files");
            if(!dataFile.exists()) {
                copyFromRawToInternal(name,"zip");
                Log.i("PackageManager","transfered files - zip");
            }
            if(!backThumb.exists()) {
                copyFromRawToInternal(name + "_back","png");
                Log.i("PackageManager","transfered files - back");
            }
            if(!frontThumb.exists()) {
                copyFromRawToInternal(name + "_front","png");
                Log.i("PackageManager","transfered files - front");
            }
            cnt++;
        }

        Log.d("tsst","in loaded");

        //load non builtIn Packages
        Log.i("PackageManager","load non-bu in");
        yaml = new Yaml();
        try {
            headerInfo = (List<HashMap<String, Object>>) yaml.load(context.openFileInput("header.yml"));
            Log.d("tsst", headerInfo.toString());
        } catch (FileNotFoundException e) {
            headerInfo = null;
        }
        if(headerInfo!=null) {
            Log.i("PackageManager","load non-bu in - got the header");
            outPackages = new MetaPackage[headerInfo.size()];
            cnt = 0;
            for (HashMap<String, Object> pkgInfo : headerInfo) {
                String name = (String) pkgInfo.get(pkgNameKey);
                List<String> colorList = (List<String>) pkgInfo.get("Color");

                Log.e("COLORS", "" + colorList);

                int[] color = new int[colorList.size()];

                ArrayList<String> tmpColors = new ArrayList<String>();

                for (String c: colorList)
                    tmpColors.add(c);

                for (int i = 0;i < tmpColors.size(); i++)
                    color[i] = Color.parseColor(tmpColors.get(i));
                int cost = (Integer) pkgInfo.get(costKey);
                int version = (Integer) pkgInfo.get(dataVersionKey);
                int rate = (Integer) pkgInfo.get(rateKey);
                String dataUrl = (String) pkgInfo.get(dataUrlKey);
                Log.d("tsst",name + " " + cost + " " + version + " " + dataUrl);
                PackageState state;
                File dataFile = new File(context.getFilesDir(),name+".zip");
                if(dataFile.exists())
                    state = PackageState.LOCAL;
                else
                    state = PackageState.REMOTE;
                if( state == PackageState.LOCAL ) {
                    int curVersion = preferences.getInt(name+"_DATA_VERSION", -1);
                    if(curVersion < version)
                        state = PackageState.REMOTE;
                }
                List<Float> bgHSVList = (List<Float>) pkgInfo.get("HSV Background");
                float[] backgroundHSV = Utils.floatListToArray(bgHSVList);
                List<Float> cbHSVList = (List<Float>) pkgInfo.get("HSV Cheat Button");
                float[] cheatButtonHSV = Utils.floatListToArray(cbHSVList);
                outPackages[cnt] = new MetaPackage(context, preferences, color, backgroundHSV, cheatButtonHSV, name, cnt+inPackages.length, state, this, rate);
                outPackages[cnt].setCost(cost);
                outPackages[cnt].setDataUrl(dataUrl);
                outPackages[cnt].setDataVersion(version);
                cnt++;
            }
        }
        packages = new MetaPackage[inPackages.length + (outPackages!=null?outPackages.length:0)];
        for (int i=0;i<inPackages.length;++i) {
            packages[i] = inPackages[i];
        }
        if(outPackages!=null) {
            for (int i = 0; i < outPackages.length; ++i) {
                packages[i + inPackages.length] = outPackages[i];
            }
        }
        generateAdapterResourceArrays();
    }

    public void generateAdapterResourceArrays() {
        ArrayList<MetaPackage> newPackagelist = new ArrayList<MetaPackage>();
        ArrayList<MetaPackage> hotPackagelist = new ArrayList<MetaPackage>();
        ArrayList<MetaPackage> localPackagelist = new ArrayList<MetaPackage>();
        for (MetaPackage pkg : packages) {
            if(pkg.getState()==PackageState.LOCAL)
                localPackagelist.add(pkg);
            if(pkg.getState()==PackageState.REMOTE || pkg.getState()==PackageState.DOWNLOADING) {
                newPackagelist.add(pkg);
                hotPackagelist.add(pkg);
            }
        }

        //convert Arraylist to Arrays for better performance
        newPackages = newPackagelist.toArray(new MetaPackage[newPackagelist.size()]);
        localPackages = localPackagelist.toArray(new MetaPackage[localPackagelist.size()]);
        hotPackages = hotPackagelist.toArray(new MetaPackage[hotPackagelist.size()]);

        Arrays.sort(hotPackages, new Comparator<MetaPackage>() {
            @Override
            public int compare(MetaPackage lhs, MetaPackage rhs) {
                if(lhs.getRate() == rhs.getRate())
                    return 0;
                if(lhs.getRate() < rhs.getRate())
                    return +1;
                return -1;
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

    public void copyFromRawToInternal(String name, String type){
        InputStream inputStream = Utils.getInputStreamFromRaw(context, name, type);
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(name+"."+type,0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Utils.pipe(inputStream,fos);
            Log.i("PackageManager","piped: " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

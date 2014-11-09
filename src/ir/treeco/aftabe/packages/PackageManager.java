package ir.treeco.aftabe.packages;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.utils.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hossein on 7/31/14.
 */
public class PackageManager {


    private final SharedPreferences preferences;
    private List<HashMap<String, Object>> headerInfo;
    private final static String pkgNameKey= "Name",
                          dataUrlKey = "Data URL",
                          costKey = "Cost",
                          dataVersionKey = "Data Version";
    Context context;

//    private Package[] packages;
//    private Package[] newPackages,localPackages,hotPackages; // for 3 tabs
    private MetaPackage[] packages;
    private MetaPackage[] newPackages, localPackages, hotPackages;


//    public  Package[] getPackages() {
//        return packages;
//    }

    public PackageManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(Utils.sharedPrefrencesTag(), Context.MODE_PRIVATE);
        //TODO uncomment below
//        refresh();
    }

    public void refresh() {
//        Package[] inPackages=null, outPackages=null;
        MetaPackage[] inPackages=null, outPackages=null;


        //load builtIn Packages
        InputStream inputStream = context.getResources().openRawResource(R.raw.header);
        Yaml yaml = new Yaml();
        headerInfo = (List<HashMap<String, Object>>) yaml.load(inputStream);
//        inPackages = new Package[headerInfo.size()];
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

            List<Float> bgHSVList = (List<Float>) pkgInfo.get("HSV Background");
            float[] backgroundHSV = Utils.floatListToArray(bgHSVList);
            List<Float> cbHSVList = (List<Float>) pkgInfo.get("HSV Cheat Button");
            float[] cheatButtonHSV = Utils.floatListToArray(cbHSVList);
//            int numberOfLevels = (Integer) pkgInfo.get(numberOfLevelsKey);
//            inPackages[cnt] = Package.getBuiltInPackage(this, cnt, context, name, desc, numberOfLevels);


            inPackages[cnt] = new MetaPackage(context, preferences, color, backgroundHSV, cheatButtonHSV, name, cnt, PackageState.LOCAL, this);
            //Copy data,Thumbnail to memory
            File dataFile = new File(context.getFilesDir(), name+".zip");
            File backThumb = new File(context.getFilesDir(), name+"_back.png");
            File frontThumb = new File(context.getFilesDir(), name+"_front.png");
            if(!dataFile.exists()) {
                copyFromRawToInternal(name,"zip");
            }
            if(!backThumb.exists()) {
                copyFromRawToInternal(name + "_back","png");
            }
            if(!frontThumb.exists()) {
                copyFromRawToInternal(name + "_front","png");
            }
            cnt++;
        }


        //load non builtIn Packages
        yaml = new Yaml();
        try {
            headerInfo = (List<HashMap<String, Object>>) yaml.load(context.openFileInput("header.yml"));
        } catch (FileNotFoundException e) {
            headerInfo = null;
        }
        if(headerInfo!=null) {
//            outPackages = new Package[headerInfo.size()];
            outPackages = new MetaPackage[headerInfo.size()];
            cnt = 0;
            for (HashMap<String, Object> pkgInfo : headerInfo) {
                String name = (String) pkgInfo.get(pkgNameKey);
//                int numberOfLevels = (Integer) pkgInfo.get(numberOfLevelsKey);
                int cost = (Integer) pkgInfo.get(costKey);
                int version = (Integer) pkgInfo.get(dataVersionKey);
                String dataUrl = (String) pkgInfo.get(dataUrlKey);
//                outPackages[cnt] = Package.getPackage(this, cnt + inPackages.length, context, name, desc, numberOfLevels, dataUrl, cost);
                PackageState state;
                File dataFile = new File(context.getFilesDir(),name+".zip");
                if(dataFile.exists())
                    state = PackageState.LOCAL;
                else
                    state = PackageState.REMOTE;
                List<Integer> colorList = (List<Integer>) pkgInfo.get("Color");
                int[] color = Utils.intListToArray(colorList);
                List<Float> bgHSVList = (List<Float>) pkgInfo.get("HSV Background");
                float[] backgroundHSV = Utils.floatListToArray(bgHSVList);
                List<Float> cbHSVList = (List<Float>) pkgInfo.get("HSV Cheat Button");
                float[] cheatButtonHSV = Utils.floatListToArray(cbHSVList);
                outPackages[cnt] = new MetaPackage(context, preferences, color, backgroundHSV, cheatButtonHSV, name, cnt+inPackages.length, state, this);
                outPackages[cnt].setCost(cost);
                outPackages[cnt].setDataUrl(dataUrl);
                outPackages[cnt].setDataVersion(version);
                cnt++;
            }
        }
//        packages = new Package[inPackages.length + (outPackages!=null?outPackages.length:0)];
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
//        ArrayList<Package> newPackagelist = new ArrayList<Package>();
//        ArrayList<Package> hotPackagelist = new ArrayList<Package>();
//        ArrayList<Package> localPackagelist = new ArrayList<Package>();
        ArrayList<MetaPackage> newPackagelist = new ArrayList<MetaPackage>();
        ArrayList<MetaPackage> hotPackagelist = new ArrayList<MetaPackage>();
        ArrayList<MetaPackage> localPackagelist = new ArrayList<MetaPackage>();
//        for(Package pkg : packages) {
        for (MetaPackage pkg : packages) {
//            if(pkg.getState()==PackageState.builtIn || pkg.getState()==PackageState.LOCAL)
            if(pkg.getState()==PackageState.LOCAL)
                localPackagelist.add(pkg);
            if(pkg.getState()==PackageState.REMOTE || pkg.getState()==PackageState.DOWNLOADING)
                newPackagelist.add(pkg);
            /**
             *
             *       how to determine HOTNESS of a package?
             *           maybe in the header file!
             *
             */
        }

        //TODO delete below
        hotPackagelist.add(packages[0]);

        //convert Arraylist to Arrays for better performance
//        newPackages = newPackagelist.toArray(new Package[newPackagelist.size()]);
//        localPackages = localPackagelist.toArray(new Package[localPackagelist.size()]);
//        hotPackages = hotPackagelist.toArray(new Package[hotPackagelist.size()]);
        newPackages = newPackagelist.toArray(new MetaPackage[newPackagelist.size()]);
        localPackages = localPackagelist.toArray(new MetaPackage[localPackagelist.size()]);
        hotPackages = hotPackagelist.toArray(new MetaPackage[hotPackagelist.size()]);
    }

//    public Package[] getHotPackages() {
//        return hotPackages;
//    }
//
//    public Package[] getLocalPackages() {
//        return localPackages;
//    }
//
//    public Package[] getNewPackages() {
//        return newPackages;
//    }

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
//        R.raw r = new R.raw();
//        Field field = null;
//        try {
//            field = R.raw.class.getDeclaredField(name);
//        } catch (NoSuchFieldException e) {
//            Log.e("pManager","field doesn't exist in R.raw class");
//        }
//        field.setAccessible(true);
//        int id = 0;
//        try {
//            id = (Integer) field.get(r);
//        } catch (IllegalAccessException e) {
//            //don't care
//        }
//        InputStream inputStream = context.getResources().openRawResource(id);
        InputStream inputStream = Utils.getInputStreamFromRaw(context, name, type);
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(name+"."+type,0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Utils.pipe(inputStream,fos);
    }

    public void downloadPackage(int id) throws Exception {
//        Package pkg = this.packages[id];
        MetaPackage pkg = this.packages[id];
        try {
            Utils.download(this.context, pkg.getDataUrl(),pkg.getName()+".zip");
        } catch (Exception e) {
            throw new Exception("can't download "+pkg.getName()+"'s data");
        }
        pkg.becomeLocal();
        generateAdapterResourceArrays(); // well :-\ not the best option ...
    }
}

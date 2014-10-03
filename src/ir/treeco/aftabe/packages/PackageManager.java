package ir.treeco.aftabe.packages;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.utils.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hossein on 7/31/14.
 */
public class PackageManager {


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
        Log.d("jijing", "pos"+1);
        for(HashMap<String,Object> pkgInfo : headerInfo) {
            String name = (String) pkgInfo.get(pkgNameKey);
            Log.d("jijing", "pos"+2);
            List<Integer> colorList = (List<Integer>) pkgInfo.get("Color");
            Log.d("jijing", colorList.toString());
            Log.d("jijing", "pos"+3);
            int[] color = Utils.intListToArray(colorList);
            Log.d("jijing", "pos"+4);
            List<Float> bgHSVList = (List<Float>) pkgInfo.get("HSV Background");
            Log.d("jijing", bgHSVList.toString());
            float[] backgroundHSV = Utils.floatListToArray(bgHSVList);
            Log.d("jijing", backgroundHSV.length+"");
            List<Float> cbHSVList = (List<Float>) pkgInfo.get("HSV Cheat Button");
            float[] cheatButtonHSV = Utils.floatListToArray(cbHSVList);
            Log.d("jijing", "pos"+5);
//            int numberOfLevels = (Integer) pkgInfo.get(numberOfLevelsKey);
//            inPackages[cnt] = Package.getBuiltInPackage(this, cnt, context, name, desc, numberOfLevels);
            inPackages[cnt] = new MetaPackage(context,color, backgroundHSV, cheatButtonHSV, name, cnt, PackageState.builtIn, this);
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
                    state = PackageState.local;
                else
                    state = PackageState.remote;
                List<Integer> colorList = (List<Integer>) pkgInfo.get("Color");
                int[] color = Utils.intListToArray(colorList);
                List<Float> bgHSVList = (List<Float>) pkgInfo.get("HSV Background");
                float[] backgroundHSV = Utils.floatListToArray(bgHSVList);
                List<Float> cbHSVList = (List<Float>) pkgInfo.get("HSV Cheat Button");
                float[] cheatButtonHSV = Utils.floatListToArray(cbHSVList);
                outPackages[cnt] = new MetaPackage(context,color, backgroundHSV, cheatButtonHSV, name, cnt+inPackages.length, state, this);
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
            if(pkg.getState()==PackageState.builtIn || pkg.getState()==PackageState.local)
                localPackagelist.add(pkg);
            if(pkg.getState()==PackageState.remote || pkg.getState()==PackageState.downloading)
                newPackagelist.add(pkg);
            /**
             *
             *       how to determine HOTNESS of a package?
             *           maybe in the header file!
             *
             */
        }
        hotPackagelist.add(packages[0]);//   JUST FOR TEST

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

//    public void copyFromRawToInternal(String name){
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
//        FileOutputStream fos = null;
//        try {
//            fos = context.openFileOutput(name+".zip",0);
//        } catch (FileNotFoundException e) {
//            Log.e("pManager","File doesn't exist in res/raw");
//        }
//        try {
//            Utils.pipe(inputStream,fos);
//        } catch (IOException e) {
//            Log.e("pManager","problem in piping");
//        }
//    }

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

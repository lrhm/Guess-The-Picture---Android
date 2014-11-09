package ir.treeco.aftabe.packages;

import android.content.Context;
import android.util.Log;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.utils.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
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
    private MetaPackage[] packages;
    private MetaPackage[] newPackages, localPackages, hotPackages;

    public PackageManager(Context context) {
        this.context = context;
        //TODO uncomment below
//        refresh();
    }

    public void refresh() {
        MetaPackage[] inPackages=null, outPackages=null;

        //load builtIn Packages
        InputStream inputStream = context.getResources().openRawResource(R.raw.header);
        Yaml yaml = new Yaml();
        headerInfo = (List<HashMap<String, Object>>) yaml.load(inputStream);
        inPackages = new MetaPackage[headerInfo.size()];
        int cnt=0;
        for(HashMap<String,Object> pkgInfo : headerInfo) {
            String name = (String) pkgInfo.get(pkgNameKey);
            List<Integer> colorList = (List<Integer>) pkgInfo.get("Color");
            int[] color = Utils.intListToArray(colorList);
            List<Float> bgHSVList = (List<Float>) pkgInfo.get("HSV Background");
            float[] backgroundHSV = Utils.floatListToArray(bgHSVList);
            List<Float> cbHSVList = (List<Float>) pkgInfo.get("HSV Cheat Button");
            float[] cheatButtonHSV = Utils.floatListToArray(cbHSVList);
            inPackages[cnt] = new MetaPackage(context,color, backgroundHSV, cheatButtonHSV, name, cnt, PackageState.LOCAL, this);

            //Copy data,Thumbnail to memory
            File dataFile = new File(context.getFilesDir(), name+".zip");
            File backThumb = new File(context.getFilesDir(), name+"_back.jpg");
            File frontThumb = new File(context.getFilesDir(), name+"_front.jpg");
            if(!dataFile.exists()) {
                copyFromRawToInternal(name,"zip");
            }
            if(!backThumb.exists()) {
                copyFromRawToInternal(name + "_back","jpg");
            }
            if(!frontThumb.exists()) {
                copyFromRawToInternal(name + "_front","jpg");
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
            outPackages = new MetaPackage[headerInfo.size()];
            cnt = 0;
            for (HashMap<String, Object> pkgInfo : headerInfo) {
                String name = (String) pkgInfo.get(pkgNameKey);
                int cost = (Integer) pkgInfo.get(costKey);
                int version = (Integer) pkgInfo.get(dataVersionKey);
                String dataUrl = (String) pkgInfo.get(dataUrlKey);
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
                outPackages[cnt] = new MetaPackage(context,color, backgroundHSV, cheatButtonHSV, name, cnt+inPackages.length, state, this);
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
            if(pkg.getState()==PackageState.REMOTE || pkg.getState()==PackageState.DOWNLOADING)
                newPackagelist.add(pkg);
            /**
             *
             *       how to determine HOTNESS of a package?
             *
             *
             */
        }

        //TODO delete below
        hotPackagelist.add(packages[0]);

        //convert Arraylist to Arrays for better performance
        newPackages = newPackagelist.toArray(new MetaPackage[newPackagelist.size()]);
        localPackages = localPackagelist.toArray(new MetaPackage[localPackagelist.size()]);
        hotPackages = hotPackagelist.toArray(new MetaPackage[hotPackagelist.size()]);
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
        Utils.pipe(inputStream,fos);
        Log.d("tsst","piped");
    }

    public void downloadPackage(int id) throws Exception {
        MetaPackage pkg = this.packages[id];
        try {
            Utils.download(this.context, pkg.getDataUrl(),pkg.getName()+".zip");
        } catch (Exception e) {
            throw new Exception("can't download "+pkg.getName()+"'s data");
        }
        pkg.becomeLocal();
        generateAdapterResourceArrays(); // well :\ not the best option ...
    }
}

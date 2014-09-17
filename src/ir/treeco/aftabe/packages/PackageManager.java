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
    private final static String pkgNameKey= "Package Name",
                          numberOfLevelsKey = "number of levels",
                          dataUrlKey = "Data url",
                          pkgDescriptionKey = "Package Description",
                          costKey = "cost";
    Context context;

    private Package[] packages;
    private Package[] newPackages,localPackages,hotPackages; // for 3 tabs

    public  Package[] getPackages() {
        return packages;
    }

    public PackageManager(Context context) {
        this.context = context;
    }

    public void refresh() {
        Package[] inPackages=null, outPackages=null;
        //load builtIn Packages
        InputStream inputStream = context.getResources().openRawResource(R.raw.header);
        Yaml yaml = new Yaml();
        headerInfo = (List<HashMap<String, Object>>) yaml.load(inputStream);
        inPackages = new Package[headerInfo.size()];
        int cnt=0;
        for(HashMap<String,Object> pkgInfo : headerInfo) {
            String name = (String) pkgInfo.get(pkgNameKey);
            String desc = (String) pkgInfo.get(pkgDescriptionKey);
            int numberOfLevels = (Integer) pkgInfo.get(numberOfLevelsKey);
            inPackages[cnt] = Package.getBuiltInPackage(this, cnt, context, name, desc, numberOfLevels);
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
            outPackages = new Package[headerInfo.size()];
            cnt = 0;
            for (HashMap<String, Object> pkgInfo : headerInfo) {
                String name = (String) pkgInfo.get(pkgNameKey);
                String desc = (String) pkgInfo.get(pkgDescriptionKey);
                int numberOfLevels = (Integer) pkgInfo.get(numberOfLevelsKey);
                int cost = 0; //(Integer) pkgInfo.get(costKey);
                String dataUrl = (String) pkgInfo.get(dataUrlKey);
                outPackages[cnt] = Package.getPackage(this, cnt + inPackages.length, context, name, desc, numberOfLevels, dataUrl, cost);
                cnt++;
            }
        }
        packages = new Package[inPackages.length + (outPackages!=null?outPackages.length:0)];
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
        ArrayList<Package> newPackagelist = new ArrayList<Package>();
        ArrayList<Package> hotPackagelist = new ArrayList<Package>();
        ArrayList<Package> localPackagelist = new ArrayList<Package>();
        for(Package pkg : packages) {
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
        newPackages = newPackagelist.toArray(new Package[newPackagelist.size()]);
        localPackages = localPackagelist.toArray(new Package[localPackagelist.size()]);
        hotPackages = hotPackagelist.toArray(new Package[hotPackagelist.size()]);
    }

    public Package[] getHotPackages() {
        return hotPackages;
    }

    public Package[] getLocalPackages() {
        return localPackages;
    }

    public Package[] getNewPackages() {
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
        Package pkg = this.packages[id];
        try {
            Utils.download(this.context, pkg.getDataUrl(),pkg.getName()+".zip");
        } catch (Exception e) {
            throw new Exception("can't download "+pkg.getName()+"'s data");
        }
        pkg.becomeLocal();
        generateAdapterResourceArrays(); // well :-\ not the best option ...
    }
}

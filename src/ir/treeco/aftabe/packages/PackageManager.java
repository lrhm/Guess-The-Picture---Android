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
                          pkgDescriptionKey = "Package Description";
    Context context;

    private Package[] packages;

    public  Package[] getPackages() {
        return packages;
    }

    public PackageManager(Context context) {
        this.context = context;
    }

    public void refresh() throws Exception {
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
            inPackages[cnt] = Package.getBuiltInPackage(cnt, context, name, desc, numberOfLevels);
            cnt++;
            Log.d("kos",inPackages[cnt-1].toString());
        }


//        //download header.yml             !!!!!!!!!!!!!!!!!SYNCHRONIZER SHOULD DOWNLOAD FILES
//        try {
//            //download(headerURL, headerFilePath);
//        } catch (Exception e) {
//            throw new Exception("problem in downloading header.yml");
//        }

        if(false) {
            //load non builtIn Packages
            yaml = new Yaml();
            headerInfo = (List<HashMap<String, Object>>) yaml.load(context.openFileInput("header.yml"));
            outPackages = new Package[headerInfo.size()];
            cnt = 0;
            for (HashMap<String, Object> pkgInfo : headerInfo) {
                String name = (String) pkgInfo.get(pkgNameKey);
                String desc = (String) pkgInfo.get(pkgDescriptionKey);
                int numberOfLevels = (Integer) pkgInfo.get(numberOfLevelsKey);
                String dataUrl = (String) pkgInfo.get(dataUrlKey);
                outPackages[cnt] = Package.getPackage(cnt + inPackages.length, context, name, desc, numberOfLevels, dataUrl);
//            //download thumbnail            !!!!!!!!!!!!!!!!!!SYNCHRONIZER SHOULD DOWNLOAD THUMBNAILS
//            try {
////                download((String) hmap.get(thumbnailUrlKey),packages[cnt].getThumbnailPath());
//            } catch (Exception e) {
//                throw new Exception("problem in downloading "+cnt+"th thumbnail");
//            }
                cnt++;
            }
        }
//        packages = new Package[inPackages.length + outPackages.length];
        packages = new Package[inPackages.length];
        for (int i=0;i<inPackages.length;++i) {
            packages[i] = inPackages[i];
        }
//        for (int i=0; i<outPackages.length; ++i) {
//            packages[i+inPackages.length] = outPackages[i];
//        }
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
    }
}

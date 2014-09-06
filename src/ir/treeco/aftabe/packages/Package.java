package ir.treeco.aftabe.packages;

import android.content.Context;
import android.util.Log;
import ir.treeco.aftabe.utils.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by hossein on 7/31/14.
 */
public class Package {
    private PackageState state;
    private int numberOfLevels, cost;
    private String name, description, dataUrl;
    private ZipFile zipFile;
    private List<HashMap<String, Object>> levelsInfo;
    private Level[] levels;
    private int id;
    private String  levelSolutionKey = "Level Solution",
                    levelAuthorKey = "Level Author";
    private Context context;

    @Override
    public String toString() {
        return state + " " + numberOfLevels + " " + name + " " + description + " " + dataUrl;
    }

    public PackageState getState() {
        return state;
    }

    public ZipFile getData() {
        return this.zipFile;
    }

    public int getNumberOfLevels() {
        return numberOfLevels;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return this.cost;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Level[] getLevels() {
        return levels;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public static Package getBuiltInPackage(int id, Context context, String name, String description, int numberOfLevels) {
        Package aPackage = new Package(id, context, name, description, numberOfLevels);
        aPackage.state = PackageState.builtIn;
        try {
            aPackage.load();
        } catch (Exception e) {
            Log.d("Package","Problem in loading "+name+" package");
        }
        return  aPackage;
    }

    public static Package getPackage(int id, Context context, String name, String description, int numberOfLevels, String dataUrl, int cost) {
        Package aPackage = new Package(id, context, name, description, numberOfLevels);
        aPackage.dataUrl = dataUrl;
        File dataFile = new File(aPackage.context.getFilesDir(),name+".zip");
        if(dataFile.exists())
            aPackage.state = PackageState.local;
        else
            aPackage.state = PackageState.remote;
        aPackage.cost = cost;
        try {
            if(aPackage.state == PackageState.local)
                aPackage.load();
        } catch (Exception e) {
            Log.d("Package","Problem in loading "+name+" package");
        }
        return  aPackage;
    }

    private Package(int id, Context context, String name, String description, int numberOfLevels) {
        this.context = context;
        this.name = name;
        this.id = id;
        this.description = description;
        this.numberOfLevels = numberOfLevels;
    }

//    public Package(Context context, String name, String description, int nuumberOfLevels, String url, int id)
//            throws FileNotFoundException {
//        this.context = context;
//        this.name = name;
//        this.numberOfLevels = nuumberOfLevels;
//        this.description = description;
//        this.dataUrl = url;
//        this.determineState();
//        this.id = id;
//        if(this.state == PackageState.local)
//            this.load();
//    }

//    public void determineState() {
//        File pkg = new File(context.getFilesDir(),this.getDataPath());
////        File pkg = new File((this.getDataPath()));
//        if(!pkg.exists()) {
//            state = PackageState.remote;
//            return;
//        }
//        state = PackageState.local;
//    }

    public void load() throws Exception {

        InputStream yamlStream=null;

        if(this.state == PackageState.builtIn) {
            ZipInputStream zipInputStream = new ZipInputStream(Utils.getInputStreamFromRaw(this.context,name,"zip"));
            ZipEntry confFile;
            while ((confFile = zipInputStream.getNextEntry()) != null) {
                if (confFile.getName().equals("config.yml"))
                    break;
            }
            if (confFile == null) {
                Log.e("package", "can't find config file in data");
                return;
            }
            yamlStream = zipInputStream;
        }

        if(this.state == PackageState.local) {
            zipFile = new ZipFile(new File(context.getFilesDir(),name+".zip"));
            ZipEntry confFile = zipFile.getEntry("config.yml");
            yamlStream = zipFile.getInputStream(confFile);
        }

        if(this.state != PackageState.builtIn && this.state != PackageState.local) {
            return;
        }

        Yaml yaml = new Yaml();
        levelsInfo = (List<HashMap<String, Object>>) yaml.load(yamlStream);
        levels = new Level[this.numberOfLevels];
        int cnt=0;
        for(HashMap<String, Object> hmap : levelsInfo) {
            Log.d("xxx", "solution: " + (String) hmap.get(levelSolutionKey));
            levels[cnt] = new Level(context, (String) hmap.get(levelAuthorKey),(String) hmap.get(levelSolutionKey),this, cnt);
            cnt++;
        }
    }

    public Level getLevel(int lev) {
        return levels[lev];
    }

    public InputStream getThumbnail() throws FileNotFoundException {
        if(this.state == PackageState.builtIn)
            return Utils.getInputStreamFromRaw(this.context, this.name+"_thumb","jpg");
        else
            return context.openFileInput(this.name+"_thumb.jpg");
    }

    public void becomeLocal() {
        this.state = PackageState.local;
        try {
            this.load();
        } catch (Exception e) {
            Log.e("Package","can't become Local");
        }
    }
}
package ir.treeco.packages;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by hossein on 7/31/14.
 */
public class Package {
    private PackageState state;
    private int numberOfLevels;
    private String name, description, dataUrl;
    private ZipFile zipFile;
    private List<HashMap<String, Object>> levelsInfo;
    private Level[] levels;
    private int id;
    private String  levelSolutionKey = "Level Solution",
                    levelAuthorKey = "Level Author";

    @Override
    public String toString() {
        return state + " " + numberOfLevels + " " + name + " " + description + " " + dataUrl + " " + zipFile;
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

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Level[] getLevels() {
        return levels;
    }

    public String getThumbnailPathInZipFile() {
        return this.name + ".jpg";
    }

    public String getConfFilePathInZipFile() {
        return "config.yml";
    }

    public String getDataPath() {
        return this.name + ".zip";
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public Package(String name, String description, int nuumberOfLevels, String url, int id)
            throws FileNotFoundException {
        this.name = name;
        this.numberOfLevels = nuumberOfLevels;
        this.description = description;
        this.dataUrl = url;
        this.determineState();
        this.id = id;
        if(this.state == PackageState.local)
            this.load();
    }

    public void determineState() {
        File pkg = new File((this.getDataPath()));
        if(!pkg.exists()) {
            state = PackageState.remote;
            return;
        }
        state = PackageState.local;
    }

    public void load() throws FileNotFoundException {
        File pkg = new File(this.getDataPath());
        try {
            zipFile = new ZipFile(pkg);
        } catch (IOException e) {
            //never gonna happen
            throw new FileNotFoundException("can't find Data file");
        }
        ZipEntry confFile = zipFile.getEntry(this.getConfFilePathInZipFile());
        Yaml yaml = new Yaml();
        try {
            levelsInfo = (List<HashMap<String, Object>>) yaml.load(zipFile.getInputStream(confFile));
        } catch (IOException e) {
            throw new FileNotFoundException("can't find config.yml");
        }
        levels = new Level[this.numberOfLevels];
        int cnt=0;
        for(HashMap<String, Object> hmap : levelsInfo) {
            levels[cnt] = new Level((String) hmap.get(levelAuthorKey),(String) hmap.get(levelSolutionKey),this, cnt);
            cnt++;
        }
    }

    public Level getLevel(int lev) {
        return levels[lev];
    }

    public InputStream getThumbnail() throws FileNotFoundException {
        try {
            return new FileInputStream(this.getThumbnailPathInZipFile());
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("can't find thumbnail file");
        }
    }
}
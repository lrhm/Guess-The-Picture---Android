package ir.treeco.aftabe.packages;

import ir.treeco.aftabe.mutlimedia.Multimedia;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by hossein on 7/31/14.
 */
public class Package {
    private ZipFile zipFile;
    private List<HashMap<String, Object>> levelsInfo;
    private Level[] levels;
    private InputStream[] thumbnails;
    private String levelSolutionKey = "Solution",
            levelAuthorKey = "Author",
            levelThumbnailKey = "Thumbnail",
            resourceInfoKey = "Resources",
            levelPrizeKey = "Prize";
    private HashMap<String, InputStream> nameToInputStream = new HashMap<String, InputStream>();

    public MetaPackage meta;

    @Override
    public String toString() {
        return meta.toString();
    }

    public int getNumberOfLevels() {
        return levels.length;
    }

    public ZipFile getData() {
        return this.zipFile;
    }

    public InputStream[] getThumbnails() {
        InputStream[] thumbnails = new InputStream[levels.length];
        try {
            for (int i = 0; i < levels.length; ++i) {
                ZipEntry entry = zipFile.getEntry(levels[i].getThumbnailName());
                thumbnails[i] = getData().getInputStream(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnails;
    }

    public Package(MetaPackage meta) {
        this.meta = meta;
        try {
            load();
        } catch (Exception e) {
            // shit happened!
            e.printStackTrace();
        }
    }

    public void load() throws Exception {

        if(meta.getState() != PackageState.LOCAL)
            return;

        InputStream yamlStream = null;

        zipFile = new ZipFile(new File(meta.getContext().getFilesDir(), meta.getName() + ".zip"));
        ZipEntry confFile = zipFile.getEntry("level_list.yml");
        yamlStream = zipFile.getInputStream(confFile);

        Yaml yaml = new Yaml();
        levelsInfo = (List<HashMap<String, Object>>) yaml.load(yamlStream);
        levels = new Level[levelsInfo.size()];

        int cnt = 0;
        for (HashMap<String, Object> aLevelInfo : levelsInfo) {

            String author = (String) aLevelInfo.get(levelAuthorKey);
            String solution = (String) aLevelInfo.get(levelSolutionKey);
            String thumbnailName = (String) aLevelInfo.get(levelThumbnailKey);
            int prize = aLevelInfo.get(levelPrizeKey)==null?30:Integer.parseInt((String) aLevelInfo.get(levelPrizeKey));
            List<HashMap<String, String>> resourcesInfo = (List<HashMap<String, String>>) aLevelInfo.get(resourceInfoKey);
            Multimedia[] resources = new Multimedia[resourcesInfo.size()];

            levels[cnt] = new Level(meta.getPreferences(), author, solution, thumbnailName, this, cnt, prize);

            int resCnt = 0;
            for( HashMap<String, String> aResource : resourcesInfo) {
                int cost = aResource.get("Cost")==null?5:Integer.parseInt(aResource.get("Cost"));
                resources[resCnt] = new Multimedia(meta.getContext(), getData(), aResource.get("Name"), aResource.get("Type"), cost);
                resCnt++;
            }
            levels[cnt].setResources(resources);
            cnt++;
        }
    }

    public Level getLevel(int lev) {
        return levels[lev];
    }
}
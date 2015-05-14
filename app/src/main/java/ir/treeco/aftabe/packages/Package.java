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
    private final static String LEVEL_SOLUTION_KEY = "Solution";
    private final static String LEVEL_AUTHOR_KEY = "Author";
    private final static String LEVEL_THUMBNAIL_KEY = "Thumbnail";
    private final static String RESOURCE_INFO_KEY = "Resources";
    private final static String LEVEL_PRIZE_KEY = "Prize";

    private ZipFile zipFile;
    private List<HashMap<String, Object>> levelsInfo;
    private Level[] levels;

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
        } catch (IOException e) {
            // Shit happened!
            e.printStackTrace();
        }
    }

    public void load() throws IOException {
        if (meta.getState() != PackageState.LOCAL)
            return;

        Yaml yaml = new Yaml();
        zipFile = new ZipFile(new File(meta.getContext().getFilesDir(), meta.getName() + ".zip"));

        InputStream yamlStream;
        {
            ZipEntry confFile = zipFile.getEntry("level_list.yml");
            yamlStream = zipFile.getInputStream(confFile);
        }

        levelsInfo = (List<HashMap<String, Object>>) yaml.load(yamlStream);
        levels = new Level[levelsInfo.size()];

        {
            int levelIndex = 0;
            for (HashMap<String, Object> aLevelInfo : levelsInfo) {
                String author = (String) aLevelInfo.get(LEVEL_AUTHOR_KEY);
                String solution = (String) aLevelInfo.get(LEVEL_SOLUTION_KEY);
                String thumbnailName = (String) aLevelInfo.get(LEVEL_THUMBNAIL_KEY);

                int prize = aLevelInfo.get(LEVEL_PRIZE_KEY) == null ? 30 : Integer.parseInt((String) aLevelInfo.get(LEVEL_PRIZE_KEY));

                List<HashMap<String, String>> resourcesInfo = (List<HashMap<String, String>>) aLevelInfo.get(RESOURCE_INFO_KEY);

                Multimedia[] resources = new Multimedia[resourcesInfo.size()];

                levels[levelIndex] = new Level(meta.getPreferences(), author, solution, thumbnailName, this, levelIndex, prize);

                {
                    int resourceIndex = 0;
                    for (HashMap<String, String> aResource : resourcesInfo) {
                        int cost = aResource.get("Cost") == null ? 5 : Integer.parseInt(aResource.get("Cost"));
                        resources[resourceIndex] = new Multimedia(meta.getContext(), getData(), aResource.get("Name"), aResource.get("Type"), cost);
                        resourceIndex++;
                    }
                }

                levels[levelIndex].setResources(resources);

                levelIndex++;
            }
        }
    }

    public Level getLevel(int index) {
        return levels[index];
    }
}
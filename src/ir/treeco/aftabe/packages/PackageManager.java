package ir.treeco.packages;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
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

    private static String headerURL = "http://192.168.1.112/sofre/header.yml";
    private List<HashMap<String, Object>> headerInfo;
    private static String pkgNameKey= "Package Name",
                          numberOfLevelsKey = "number of levels",
                          thumbnailUrlKey = "thumbnail url",
                          dataUrlKey = "Data url",
                          pkgDescriptionKey = "Package Description";

    private Package[] packages;

    public  Package[] getPackages() {
        return packages;
    }

    public String getHeaderPath() {
        return "header.yml";
    }

    public void refresh() throws Exception {
        //download header.yml
        try {
            download(headerURL, this.getHeaderPath());
        } catch (Exception e) {
            throw new Exception("problem in downloading header.yml");
        }

        //load header.yml
        Yaml yaml = new Yaml();
        headerInfo = (List<HashMap<String, Object>>) yaml.load(new FileInputStream("header.yml"));

        //generate packages
        packages = new Package[headerInfo.size()];
        int cnt=0;
        for(HashMap<String, Object> hmap : headerInfo) {
            packages[cnt] = new Package((String)hmap.get(pkgNameKey),
                    (String) hmap.get(pkgDescriptionKey),
                    (Integer)hmap.get(numberOfLevelsKey),
                    (String) hmap.get(dataUrlKey),
                    cnt);

            //download thumbnail
            try {
                download((String) hmap.get(thumbnailUrlKey),packages[cnt].getThumbnailPathInZipFile());
            } catch (Exception e) {
                throw new Exception("problem in downloading "+cnt+"th thumbnail");
            }

            cnt++;
        }
    }

    public void downloadPackage(int id) throws Exception {
        Package pkg = this.packages[id];
        try {
            download(pkg.getDataUrl(),pkg.getDataPath());
        } catch (Exception e) {
            throw new Exception("can't download "+pkg.getName()+"'s data");
        }
        pkg.determineState();
    }

    public static void download(String url, String path) throws Exception {
        URL source = null;
        try {
            source = new URL(url);
        } catch (MalformedURLException e) {
            throw new Exception("Bad url",e);
        }
        ReadableByteChannel rbc = Channels.newChannel(source.openStream());
        FileOutputStream fos = new FileOutputStream(path);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
}

package ir.treeco.aftabe.packages;

import android.util.Log;
import ir.treeco.aftabe.mutlimedia.Multimedia;
import ir.treeco.aftabe.utils.LengthManager;
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
    //    private PackageState state;
//    private int cost;
//    private String name, description, dataUrl;
    private ZipFile zipFile;
    private List<HashMap<String, Object>> levelsInfo;
    private Level[] levels;
    private InputStream[] thumbnails;
    //    private int id;
    private String levelSolutionKey = "Solution",
            levelAuthorKey = "Author",
            levelThumbnailKey = "Thumbnail",
            resourceInfoKey = "Resources",
            levelPrizeKey = "Prize";
    private HashMap<String, InputStream> nameToInputStream = new HashMap<String, InputStream>();
//    private Context context;
//    private PackageManager pManager;

    public MetaPackage meta;

    @Override
    public String toString() {
        return meta.toString();
    }

    public int getNumberOfLevels() {
        return levels.length;
    }

    //    public PackageState getState() {
//        return state;
//    }
//
    public ZipFile getData() {
        return this.zipFile;
    }
//
//    public String getName() {
//        return name;
//    }
//
//    public int getCost() {
//        return this.cost;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public Level[] getLevels() {
//        return levels;
//    }
//
//    public String getDataUrl() {
//        return dataUrl;
//    }

//    public static Package getBuiltInPackage(PackageManager pManager, int id, Context context, String name, String description, int numberOfLevels) {
//        Package aPackage = new Package(pManager, id, context, name, description, numberOfLevels);
//        aPackage.state = PackageState.builtIn;
//        try {
//            aPackage.load();
//        } catch (Exception e) {
//            Log.d("Package","Problem in loading "+name+" package");
//        }
//        return  aPackage;
//    }

//    public static Package getPackage(PackageManager pManager, int id, Context context, String name, String description, int numberOfLevels, String dataUrl, int cost) {
//        Log.d("inMetro","int Package factory: "+ name);
//        Package aPackage = new Package(pManager, id, context, name, description, numberOfLevels);
//        aPackage.dataUrl = dataUrl;
//        File dataFile = new File(aPackage.context.getFilesDir(),name+".zip");
//        if(dataFile.exists())
//            aPackage.state = PackageState.local;
//        else
//            aPackage.state = PackageState.remote;
//        aPackage.cost = cost;
//        try {
//            if(aPackage.state == PackageState.local)
//                aPackage.load();
//        } catch (Exception e) {
//            Log.d("Package","Problem in loading "+name+" package");
//        }
//        return  aPackage;
//    }

//    private Package(PackageManager pManager, int id, Context context, String name, String description, int numberOfLevels) {
//        this.pManager = pManager;
//        this.context = context;
//        this.name = name;
//        this.id = id;
//        this.description = description;
//        this.numberOfLevels = numberOfLevels;
//    }

    public InputStream getInputStreamByName(String name) {
        ByteArrayOutputStream baos=null;
        try {
            InputStream is = nameToInputStream.get(name);
            //duplicate inputStream
            byte[] buffer = new byte[1024];
            int count;
            baos = new ByteArrayOutputStream();
            while ((count = is.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
            nameToInputStream.put(name, new ByteArrayInputStream(baos.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public InputStream[] getThumbnails() {
        try {
            int cnt = 0;
            for (HashMap<String, Object> aLevelInfo : levelsInfo) {
                List<HashMap<String, String>> resourcesInfo = (List<HashMap<String, String>>) aLevelInfo.get(resourceInfoKey);
                for (HashMap<String, String> aResource : resourcesInfo) {
                    if (aResource.get("Name").equals(levels[cnt].getThumbnailName())) {
                        //duplicate inputStream
                        InputStream is = getInputStreamByName(aResource.get("Name"));
                        byte[] buffer = new byte[1024];
                        int count;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        while ((count = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, count);
                        }
                        levels[cnt].setThumbnail(new ByteArrayInputStream(baos.toByteArray()));
                        thumbnails[cnt] = new ByteArrayInputStream(baos.toByteArray());
                        break;
                    }
                }
                cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnails;
    }


    public Package(MetaPackage meta) {
//        this.pManager = meta.getPackageManager();
//        this.context = meta.getContext();
//        this.name = meta.getName();
//        this.id = meta.getId();
//        this.description = meta.getDescription();
//        this.cost = meta.getCost();
//        this.dataUrl = meta.getDataUrl();
        this.meta = meta;
        try {
            load();
        } catch (Exception e) {
            // shit happened!
            e.printStackTrace();
        }
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

    // 'this' replaced with 'meta' after changes
    public void load() throws Exception {

        InputStream yamlStream = null;

        if (meta.getState() == PackageState.builtIn) {
            ZipInputStream zipInputStream = new ZipInputStream(Utils.getInputStreamFromRaw(meta.getContext(), meta.getName(), "zip"));
            ZipEntry confFile;
            while ((confFile = zipInputStream.getNextEntry()) != null) {
                if (confFile.getName().equals("level_list.yml"))
                    break;
            }
            if (confFile == null) {
                Log.e("package", "can't find config file in data");
                return;
            }
            yamlStream = zipInputStream;
        }

        if (meta.getState() == PackageState.local) {
            zipFile = new ZipFile(new File(meta.getContext().getFilesDir(), meta.getName() + ".zip"));
            ZipEntry confFile = zipFile.getEntry("level_list.yml");
            yamlStream = zipFile.getInputStream(confFile);
        }

        if (meta.getState() != PackageState.builtIn && meta.getState() != PackageState.local) {
            return;
        }

        Yaml yaml = new Yaml();
        levelsInfo = (List<HashMap<String, Object>>) yaml.load(yamlStream);
//        levels = new Level[this.numberOfLevels];
        levels = new Level[levelsInfo.size()];
        thumbnails = new InputStream[levelsInfo.size()];


        nameToInputStream = new HashMap<String, InputStream>();
        if (meta.getState() == PackageState.builtIn) {
            ZipInputStream zipInputStream = new ZipInputStream(Utils.getInputStreamFromRaw(meta.getContext(), meta.getName(), "zip"));
            ZipEntry zipFile;
            while ((zipFile = zipInputStream.getNextEntry()) != null) {
                String fname = zipFile.getName();
                byte[] buffer = new byte[1024];
                int count;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((count = zipInputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                }
                nameToInputStream.put(fname, new ByteArrayInputStream(baos.toByteArray()));
            }
        }


        int cnt = 0;
        for (HashMap<String, Object> aLevelInfo : levelsInfo) {

            String author = (String) aLevelInfo.get(levelAuthorKey);
            String solution = (String) aLevelInfo.get(levelSolutionKey);
            String thumbnailName = (String) aLevelInfo.get(levelThumbnailKey);
            int prize = aLevelInfo.get(levelPrizeKey)==null?30:Integer.parseInt((String) aLevelInfo.get(levelPrizeKey));
            List<HashMap<String, String>> resourcesInfo = (List<HashMap<String, String>>) aLevelInfo.get(resourceInfoKey);
            Multimedia[] resources = new Multimedia[resourcesInfo.size()];

            levels[cnt] = new Level(meta.getContext(), author, solution, thumbnailName, this, cnt, prize);

            int resCnt = 0;
            if (meta.getState() == PackageState.builtIn) {
                for (HashMap<String, String> aResource : resourcesInfo) {
//                    while ((zipFile = zipInputStream.getNextEntry()) != null) {
//                        String fname = zipFile.getName();
//                        if (aResource.get("Name").equals(fname)) {
//                            byte[] buffer = new byte[1024];
//                            int count;
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            while ((count = zipInputStream.read(buffer)) != -1) {
//                                baos.write(buffer, 0, count);
//                            }
                    int cost = aResource.get("Cost")==null?0:Integer.parseInt(aResource.get("Cost"));
                    if(aResource.get("Name").equals(thumbnailName)) {
                        //duplicate inputStream
                        InputStream is = getInputStreamByName(aResource.get("Name"));
                        byte[] buffer = new byte[1024];
                        int count;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        while ((count = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, count);
                        }
                        levels[cnt].setThumbnail(new ByteArrayInputStream(baos.toByteArray()));
                        thumbnails[cnt] = new ByteArrayInputStream(baos.toByteArray());
                        resources[resCnt] = new Multimedia(aResource.get("Type"), new ByteArrayInputStream(baos.toByteArray()), cost);
                    }
                    else {
                        resources[resCnt] = new Multimedia(aResource.get("Type"), getInputStreamByName(aResource.get("Name")), cost);
                    }
                    resCnt++;
//                        }
                    }
            }
            if (meta.getState() == PackageState.local) {
                for (HashMap<String, String> aResource : resourcesInfo) {
                    ZipEntry entry = getData().getEntry(aResource.get("Name"));
                    InputStream is = getData().getInputStream(entry);
                    if (aResource.get("Name").equals(thumbnailName)) {
                        //duplicate input stream
                        byte[] buffer = new byte[1024];
                        int count;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        while ((count = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, count);
                        }
                        levels[cnt].setThumbnail(new ByteArrayInputStream(baos.toByteArray()));
                        thumbnails[cnt] = new ByteArrayInputStream(baos.toByteArray());
                        int cost = aResource.get("Cost")==null?0:Integer.parseInt(aResource.get("Cost"));
                        resources[resCnt] = new Multimedia(aResource.get("Type"), new ByteArrayInputStream(baos.toByteArray()),cost);
                    } else {
                        int cost = aResource.get("Cost")==null?0:Integer.parseInt(aResource.get("Cost"));
                        resources[resCnt] = new Multimedia(aResource.get("Type"), is, cost);
                    }
                    resCnt++;
                }
            }
            levels[cnt].setResources(resources);
            cnt++;
        }
//        loadInputStreams();

//        //set Levels Images
//        int myWidth = LengthManager.getScreenWidth() / 2;
//        int myHeight = myWidth;
//        if(meta.getState() == PackageState.builtIn) {
//            ZipInputStream zipInputStream = new ZipInputStream(Utils.getInputStreamFromRaw(meta.getContext(),meta.getName(),"zip"));
//            ZipEntry confFile;
//            while ((confFile = zipInputStream.getNextEntry()) != null) {
//                String fname = confFile.getName();
//                if (fname.matches("\\d+[.]jpg")) {
//                    fname = fname.split("[.]")[0];
//                    int idx = Integer.parseInt(fname);
//                    byte[] buffer = new byte[1024];
//                    int count;
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    while ((count = zipInputStream.read(buffer)) != -1) {
//                        baos.write(buffer, 0, count);
//                    }
////                    levels[idx].setImage(ImageManager.loadImageFromInputStream(new ByteArrayInputStream(baos.toByteArray()),myWidth,myHeight));
//                    levels[idx].setImage(new ByteArrayInputStream(baos.toByteArray()));
//                }
//            }
//        }
//        if(meta.getState() == PackageState.local) {
////            for(int i=0; i<this.numberOfLevels; ++i) {
//            for(int i=0; i<levels.length; ++i) {
//                ZipEntry entry = this.getData().getEntry(i + ".jpg");
////                levels[i].setImage(ImageManager.loadImageFromInputStream(getData().getInputStream(entry),myWidth,myHeight));
//                levels[i].setImage(this.getData().getInputStream(entry));
//            }
//        }
    }

//    public void loadInputStreams() throws IOException {
//        int myWidth = LengthManager.getScreenWidth() / 2;
//        int myHeight = myWidth;
//        if(meta.getState() == PackageState.builtIn) {
//            ZipInputStream zipInputStream = new ZipInputStream(Utils.getInputStreamFromRaw(meta.getContext(),meta.getName(),"zip"));
//            ZipEntry confFile;
//            while ((confFile = zipInputStream.getNextEntry()) != null) {
//                String fname = confFile.getName();
//                if (fname.matches("\\d+[.]jpg")) {
//                    fname = fname.split("[.]")[0];
//                    int idx = Integer.parseInt(fname);
//                    byte[] buffer = new byte[1024];
//                    int count;
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    while ((count = zipInputStream.read(buffer)) != -1) {
//                        baos.write(buffer, 0, count);
//                    }
////                    levels[idx].setImage(ImageManager.loadImageFromInputStream(new ByteArrayInputStream(baos.toByteArray()),myWidth,myHeight));
//                    levels[idx].setThumbnail(new ByteArrayInputStream(baos.toByteArray()));
//                }
//            }
//        }
//        if(meta.getState() == PackageState.local) {
////            for(int i=0; i<this.numberOfLevels; ++i) {
//            for(int i=0; i<levels.length; ++i) {
//                ZipEntry entry = this.getData().getEntry(i + ".jpg");
////                levels[i].setImage(ImageManager.loadImageFromInputStream(getData().getInputStream(entry),myWidth,myHeight));
//                levels[i].setThumbnail(this.getData().getInputStream(entry));
//            }
//        }
//    }

    public Level getLevel(int lev) {
        return levels[lev];
    }

//    public InputStream getFront() throws FileNotFoundException {
//        if(this.state == PackageState.builtIn)
//            return Utils.getInputStreamFromRaw(this.context, this.name+"_front","jpg");
//        else
//            return context.openFileInput(this.name+"_front.jpg");
//    }
//
//    public InputStream getBack() throws FileNotFoundException {
//        if (this.state == PackageState.builtIn)
//            return Utils.getInputStreamFromRaw(this.context, this.name + "_back", "jpg");
//        else
//            return context.openFileInput(this.name + "_back.jpg");
//    }


    /*public InputStream getFront(){
        if(this.state == PackageState.builtIn)
            return Utils.getInputStreamFromRaw(this.context, this.name+"_front","jpg");
        else
            try {
                return context.openFileInput(this.name+"_front.jpg");
            } catch (FileNotFoundException e) {
                return null;
            }
    }

    public InputStream getBack() {
        if(this.state == PackageState.builtIn)
            return Utils.getInputStreamFromRaw(this.context, this.name+"_back","jpg");
        else
            try {
                return context.openFileInput(this.name+"_back.jpg");
            } catch (FileNotFoundException e) {
                return null;
            }
    }*/

//    public void becomeLocal() {
//        this.state = PackageState.local;
//        try {
////            Utils.download(this.context, dataUrl, this.getName()+".zip", new NotificationProgressListener(context, this));
//            Utils.download(this.context, "http://static.treeco.ir/packages/remoteAftabe.zip", this.getName()+".zip", new NotificationProgressListener(context, this));
//            this.load();
//        } catch (Exception e) {
//            e.printStackTrace();
//            this.state = PackageState.remote;
//        }
//        pManager.generateAdapterResourceArrays();
//    }
}
package ir.treeco.aftabe.mutlimedia;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by hossein on 9/21/14.
 */
public class Multimedia {
    private MultimediaType type;
//    private InputStream media;
    private String path;
    private int cost;
    private Context context;
    private ZipFile zipFile;

    public MultimediaType getType() {
        return type;
    }

    public InputStream getMedia() {
        InputStream is = null;
        try {
            ZipEntry entry = zipFile.getEntry(path);
            is = zipFile.getInputStream(entry);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCost() {
        return cost;
    }

//    public void setMedia(InputStream media) {
//        media = this.media;
//    }

//    public Multimedia(MultimediaType type, InputStream media, int cost) {
//        this.type = type;
//        this.media = media;
//        this.cost = cost;
//    }
//
//    public Multimedia(String typeStr, InputStream media, int cost) {
//        if(typeStr.equals("IMAGE"))
//            this.type = MultimediaType.IMAGE;
//        if(typeStr.equals("AUDIO"))
//            this.type = MultimediaType.AUDIO;
//        if(typeStr.equals("VIDEO"))
//            this.type = MultimediaType.VIDEO;
//        this.media = media;
//        this.cost = cost;
//    }

//    public Multimedia(MultimediaType type, int cost) {
//        this.type = type;
//        this.cost = cost;
//    }

    public Multimedia(Context context, ZipFile zipFile, String path, String typeStr, int cost) {
        this.context = context;
        this.cost = cost;
        this.path = path;
        this.zipFile = zipFile;
        if(typeStr.equals("IMAGE"))
            this.type = MultimediaType.IMAGE;
        if(typeStr.equals("AUDIO"))
            this.type = MultimediaType.AUDIO;
        if(typeStr.equals("VIDEO"))
            this.type = MultimediaType.VIDEO;
    }

    public static enum MultimediaType {
        VIDEO, AUDIO, IMAGE;
    }
}
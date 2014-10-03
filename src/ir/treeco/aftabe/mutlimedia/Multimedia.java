package ir.treeco.aftabe.mutlimedia;

import java.io.InputStream;

/**
 * Created by hossein on 9/21/14.
 */
public class Multimedia {
    private MultimediaType type;
    private InputStream media;
    private int cost;

    public MultimediaType getType() {
        return type;
    }

    public InputStream getMedia() {
        return media;
    }

    public void setMedia(InputStream media) {
        media = this.media;
    }

    public Multimedia(MultimediaType type, InputStream media, int cost) {
        this.type = type;
        this.media = media;
        this.cost = cost;
    }

    public Multimedia(String typeStr, InputStream media, int cost) {
        if(typeStr.equals("IMAGE"))
            this.type = MultimediaType.IMAGE;
        if(typeStr.equals("AUDIO"))
            this.type = MultimediaType.AUDIO;
        if(typeStr.equals("VIDEO"))
            this.type = MultimediaType.VIDEO;
        this.media = media;
        this.cost = cost;
    }

    public Multimedia(MultimediaType type, int cost) {
        this.type = type;
        this.cost = cost;
    }

    public static enum MultimediaType {
        VIDEO, AUDIO, IMAGE;
    }
}
package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe2.Util.Logger;
import ir.treeco.aftabe2.Util.Tools;

/**
 * Created by al on 5/14/16.
 */
public class ForceObject {

    @Expose
    boolean forceUpdate;

    @Expose
    boolean forceDownload;

    @Expose
    int version;

    @Expose
    URLHolder file;

    @Expose
    boolean active;


    @Expose
    URLHolder image;

    public boolean isActive() {
        return active;
    }

    public int getVersionId() {
        return version;
    }

    public String getUrl() {
        return Tools.getUrl() + "api/files/version/download/" + file.name;
    }

    public String getImageURL() {
        return Tools.getUrl() + "api/pictures/version/download/" + image.name;

    }

    public int getSize() {
        return file.size;
    }

    public String getName() {
        return file.name;
    }

    public boolean isForceDownload() {
        return forceDownload;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public class URLHolder {

        @Expose
        String name;

        @Expose
        int size;
    }

}

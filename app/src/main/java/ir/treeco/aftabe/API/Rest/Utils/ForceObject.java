package ir.treeco.aftabe.API.Rest.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 5/14/16.
 */
public class ForceObject {

    @Expose
    boolean forceUpdate;

    @Expose
    boolean forceDownload;

    @Expose
    int versionId;

    @Expose
    String url;


    public int getVersionId() {
        return versionId;
    }

    public String getUrl() {
        return url;
    }

    public String getName(){
        return url;
    }

    public boolean isForceDownload() {
        return forceDownload;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

}

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
    int version;

    @Expose
    URLHolder file;


    public int getVersionId() {
        return version;
    }

    public String getUrl() {
        return file.name;
    }

    public String getName(){
        return file.name;
    }

    public boolean isForceDownload() {
        return forceDownload;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public class URLHolder{

        @Expose
        String name;
    }

}

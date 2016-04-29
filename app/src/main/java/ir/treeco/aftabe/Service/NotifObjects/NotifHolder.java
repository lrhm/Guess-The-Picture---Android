package ir.treeco.aftabe.Service.NotifObjects;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 4/28/16.
 */
public class NotifHolder {

    @Expose
    NotifData data;

    @Expose
    String date;

    @Expose
    boolean seen;

    @Expose
    String type;

    public String getType() {
        return type;
    }

    public boolean isSeen() {
        return seen;
    }

    public String getDate() {
        return date;
    }

    public NotifData getData() {
        return data;
    }

}

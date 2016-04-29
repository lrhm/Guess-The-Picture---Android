package ir.treeco.aftabe.Service.NotifObjects;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 4/28/16.
 */
public class NotifData {


    @Expose
    String from;

    public String getFromUserId() {
        return from;
    }

}

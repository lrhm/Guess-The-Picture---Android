package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by al on 6/1/16.
 */
public class AppListHolder {

    @Expose
    ArrayList<String> apps;

    public AppListHolder(ArrayList<String> apps) {
        this.apps = apps;
    }
}

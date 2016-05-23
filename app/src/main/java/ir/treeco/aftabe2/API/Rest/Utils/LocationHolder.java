package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by root on 5/5/16.
 */
public class LocationHolder {

    @Expose
    double lat;

    @Expose
    double lng;

    @Expose
    float acc;

    public LocationHolder(double lat, double lng, float acc) {
        this.lat = lat;
        this.lng = lng;
        this.acc = acc;
    }
}

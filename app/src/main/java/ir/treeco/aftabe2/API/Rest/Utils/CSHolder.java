package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 5/31/16.
 */
public class CSHolder {

    @Expose
    String in;

    @Expose
    Long date;


    public CSHolder(String in, Long date) {
        this.in = in;
        this.date = date;
    }
}

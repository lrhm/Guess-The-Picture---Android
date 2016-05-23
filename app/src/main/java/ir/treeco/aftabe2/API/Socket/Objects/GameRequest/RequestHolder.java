package ir.treeco.aftabe2.API.Socket.Objects.GameRequest;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by al on 3/15/16.
 */
public class RequestHolder {

    @Expose
    String time;

    public RequestHolder() {
        Date date1 = new Date();
        SimpleDateFormat x = new SimpleDateFormat("yyyy-MM-dd' 'hh:mm:ss'.'SSS Z");
        time = x.format(date1);
    }
}

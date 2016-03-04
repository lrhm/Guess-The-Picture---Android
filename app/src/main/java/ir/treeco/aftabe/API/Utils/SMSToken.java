package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/4/16.
 */
public class SMSToken {

    @Expose
    public String phone;

    @Expose
    public String created;

    @Expose
    public String code;

    @Expose
    public String smsTokenId;

    public String id;

    @Expose
    public String imei;

    @Expose
    public String name;

    public void update(String imei, String name, String code) {
        this.imei = imei;
        this.name = name;
        this.smsTokenId = id;
        this.code = code;
    }
}

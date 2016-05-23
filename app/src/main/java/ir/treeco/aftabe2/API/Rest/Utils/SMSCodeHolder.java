package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/10/16.
 */
public class SMSCodeHolder {

    @Expose
    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

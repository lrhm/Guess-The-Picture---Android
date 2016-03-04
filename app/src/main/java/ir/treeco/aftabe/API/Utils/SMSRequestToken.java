package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/4/16.
 */
public class SMSRequestToken {

    @Expose
    public String phone;

    public SMSRequestToken(String phoneNumber) {
        phone = phoneNumber;
    }
}

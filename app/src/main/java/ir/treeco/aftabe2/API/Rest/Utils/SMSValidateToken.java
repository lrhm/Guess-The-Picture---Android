package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/10/16.
 */
public class SMSValidateToken {

    @Expose
    String phone;

    @Expose
    String created;

    @Expose
    String id;

    @Expose
    boolean olduser;

    public boolean isOlduser() {
        return olduser;
    }

    public String getPhone() {
        return phone;
    }

    public String getId() {
        return id;
    }

    public String getCreated() {
        return created;
    }

}


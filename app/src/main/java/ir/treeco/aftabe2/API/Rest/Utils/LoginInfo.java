package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by al on 3/4/16.
 */
public class LoginInfo {


    @Expose
    @SerializedName("id")
    public String accessToken;

    @Expose
    public int ttl;

    @Expose
    public String created;

    @Expose
    public String userId;

    @Expose
    public String model;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getAccessToken() {
        return accessToken;
    }

        public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Expose
    public String phone;



}

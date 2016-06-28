package ir.treeco.aftabe2.API.Socket.Objects.Notifs;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 6/28/16.
 */
public class AdNotification {

    @Expose
    String title;

    @Expose
    String content;

    @Expose
    String imgUrl;

    @Expose
    String redirectURL;


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getRedirectURL() {
        return redirectURL;
    }
}

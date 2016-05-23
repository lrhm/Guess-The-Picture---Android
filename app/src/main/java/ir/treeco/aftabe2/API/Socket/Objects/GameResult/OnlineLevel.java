package ir.treeco.aftabe2.API.Socket.Objects.GameResult;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/14/16.
 */
public class OnlineLevel {

    @Expose
    String id;

    @Expose
    String answer;

    @Expose
    ImageObject image;

    public String getAnswer() {
        return answer;
    }

    public String getUrl() {
        try {
            return image.name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getId() {
        return id;
    }
}

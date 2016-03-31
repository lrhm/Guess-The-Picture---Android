package ir.treeco.aftabe.API.Socket.Objects.GameResult;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

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

package ir.treeco.aftabe.API.Socket.Objects.GameResult;

import com.google.gson.Gson;
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
    String image;

    public String getUrl() {
        try {
            JSONObject jsonObject = new JSONObject(image);
            return jsonObject.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

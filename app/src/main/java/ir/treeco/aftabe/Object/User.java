package ir.treeco.aftabe.Object;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by al on 12/26/15.
 */
public class User {
    private String userName;
    private String name;
    private int mark;
    private int rank;
    private int id;


    public User(JSONObject jsonObject) {
        try {
            userName = jsonObject.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            id = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mark = jsonObject.getInt("mark");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            rank = jsonObject.getInt("rank");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User(String userName, String name, int mark, int rank, int id) {
        this.userName = userName;
        this.mark = mark;
        this.rank = rank;
        this.id = id;
        this.name = name;
    }

    public User(String userName, int mark, int rank) {
        this.userName = userName;
        this.mark = mark;
        this.rank = rank;
    }

    public User(String userName, int mark) {
        this.userName = userName;
        this.mark = mark;
    }

    public int getRank() {
        return rank;
    }

    public String getUserName() {
        return userName;
    }

    public int getMark() {
        return mark;
    }

    public int getId() {
        return id;
    }



    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("mark", mark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("rank", rank);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

}

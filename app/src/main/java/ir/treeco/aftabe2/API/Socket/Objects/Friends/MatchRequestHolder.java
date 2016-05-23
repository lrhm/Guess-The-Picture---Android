package ir.treeco.aftabe2.API.Socket.Objects.Friends;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by al on 4/28/16.
 * user to server
 * user dst must be friends with src -> request match
 *
 * server to user , match requested
 */
public class MatchRequestHolder {


    @Expose
    String friendId;

    @Expose
    String time;

    public MatchRequestHolder(String friendId){
        Date date1 = new Date();
        SimpleDateFormat x = new SimpleDateFormat("yyyy-MM-dd' 'hh:mm:ss'.'SSS Z");
        time = x.format(date1);
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getTime() {
        return time;
    }

}

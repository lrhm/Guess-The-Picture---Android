package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 4/25/16.
 */
public class FriendRequestSent {

//    "created": "2016-04-25T08:50:38.266Z",
//            "id": "571dda5e1562913f774bd1da",
//            "friendId": "571a26fdb15b818f3893102a",
//            "userId": "56f4f6e78f046e7310895c2c"

    @Expose
    String created;

    @Expose
    String id;

    @Expose
    String friendId;

    @Expose
    String userId;
}

package ir.treeco.aftabe2.API.Rest.Interfaces;

import ir.treeco.aftabe2.Object.User;

/**
 * Created by al on 3/13/16.
 */
public interface BatchUserFoundListener {

    void onGotUserList(User[] users);

    void onGotError();

}

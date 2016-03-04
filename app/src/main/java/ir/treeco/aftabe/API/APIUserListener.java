package ir.treeco.aftabe.API;

import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 3/4/16.
 */
public interface APIUserListener {

    void onGetUser(User user);

    void onGetError();
}

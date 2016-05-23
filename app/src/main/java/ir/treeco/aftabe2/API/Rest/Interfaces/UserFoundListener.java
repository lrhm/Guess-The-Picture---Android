package ir.treeco.aftabe2.API.Rest.Interfaces;

import ir.treeco.aftabe2.Object.User;

/**
 * Created by al on 3/4/16.
 */
public interface UserFoundListener {

    void onGetUser(User user);

    void onGetError();

    void onGetMyUser(User myUser);

    void onForceLogout();

}

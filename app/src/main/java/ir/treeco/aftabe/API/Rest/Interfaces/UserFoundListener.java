package ir.treeco.aftabe.API.Rest.Interfaces;

import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 3/4/16.
 */
public interface UserFoundListener {

    void onGetUser(User user);

    void onGetError();

    void onGetMyUser(User myUser);

    void onForceLogout();

}

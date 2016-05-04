package ir.treeco.aftabe.API;

import ir.treeco.aftabe.API.Utils.GoogleToken;
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

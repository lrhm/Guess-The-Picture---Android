package ir.treeco.aftabe.API.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/6/16.
 */
public class UsernameCheck {

    @Expose
    int count;

    public boolean isUsernameAccessible() {
        return count == 0;
    }
}

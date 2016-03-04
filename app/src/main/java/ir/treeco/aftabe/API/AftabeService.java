package ir.treeco.aftabe.API;

import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.API.Utils.GuestCreateToken;
import ir.treeco.aftabe.API.Utils.SMSRequestToken;
import ir.treeco.aftabe.API.Utils.SMSToken;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.API.Utils.LoginInfo;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by al on 3/4/16.
 */
public interface AftabeService {

    @POST("/api/users/login")
    Call<LoginInfo> getMyUserLogin(@Body GoogleToken googleToken);

    @POST("/api/users/login")
    Call<LoginInfo> getGuestUserLogin(@Body GuestCreateToken guestCreate);

    @POST("/api/smstokens")
    Call<SMSToken> getSMSToken(@Body SMSRequestToken smsToken);

    @POST("/api/users/login")
    Call<LoginInfo> getSMSUserLogin(@Body SMSToken smsToken);

    @GET("/api/users/me")
    Call<User> getMyUser(@Query("access_token") String accessToken);

    @GET("/api/users/{user_id}")
    Call<User> getUser(@Query("user_atk") String accessToken, @Path("user_id") String userId);

}
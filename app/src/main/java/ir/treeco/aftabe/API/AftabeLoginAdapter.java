package ir.treeco.aftabe.API;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.API.Utils.GuestCreateToken;
import ir.treeco.aftabe.API.Utils.SMSRequestToken;
import ir.treeco.aftabe.API.Utils.SMSToken;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.API.Utils.LoginInfo;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by al on 3/4/16.
 */
public class AftabeLoginAdapter {

    private static Retrofit retrofit;
    private static AftabeService aftabeService;
    private final static String baseUrl = "https://aftabe2.com:2020";

    private static void init() {
        if (retrofit == null) {

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            aftabeService = retrofit.create(AftabeService.class);
        }

    }

    public static void createGuestUser(final APIUserListener apiUserListener) {

        init();

//        TODO check if random is saved in sd
        Random random = new Random(System.currentTimeMillis());

        String rand = "";

        for (int i = random.nextInt(79); i < random.nextInt(85); i++) {
            rand = random.nextLong() + "";
            if (i % 5 == 0)
                break;
        }
        if (rand.compareTo("") == 0)
            rand = random.nextLong() + "";

        GuestCreateToken guestCreateToken = new GuestCreateToken(rand);

        Call<LoginInfo> call = aftabeService.getGuestUserLogin(guestCreateToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {
                final LoginInfo loginInfo = response.body();

                Call<User> c = aftabeService.getMyUser(loginInfo.getAccessToken());
                c.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Response<User> response) {
                        response.body().setLoginInfo(loginInfo);
                        apiUserListener.onGetUser(response.body());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        apiUserListener.onGetError();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                apiUserListener.onGetError();
            }
        });

    }

    public static void getUser(User myUser, String otherUserId, final APIUserListener apiUserListener) {

        init();

        Call<User> call = aftabeService.getUser(myUser.getLoginInfo().accessToken, otherUserId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                apiUserListener.onGetUser(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                apiUserListener.onGetError();

            }
        });
    }

    public static void submitSMSActivation(SMSToken smsToken, String imei,
                                           String name, String validationCode, final APIUserListener apiUserListener) {
        init();
        smsToken.update(imei, name, validationCode);
        Call<LoginInfo> call = aftabeService.getSMSUserLogin(smsToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {

                final LoginInfo loginInfo = response.body();
                Call<User> c = aftabeService.getMyUser(loginInfo.accessToken);
                c.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Response<User> response) {
                        response.body().setLoginInfo(loginInfo);
                        apiUserListener.onGetUser(response.body());
                    }

                    @Override
                    public void onFailure(Throwable t) {

                        apiUserListener.onGetError();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                apiUserListener.onGetError();
            }
        });
    }

    public static void requestSMSActivation(SMSRequestToken smsRequestToken, final SMSValidationListener smsValidationListener) {


        init();

        Call<SMSToken> smsTokenCall = aftabeService.getSMSToken(smsRequestToken);
        smsTokenCall.enqueue(new Callback<SMSToken>() {
            @Override
            public void onResponse(Response<SMSToken> response) {

                Log.d("TAG", "got first response");
                smsValidationListener.onSMSValidateSent(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                smsValidationListener.onSMSValidationFail();
                Log.d("TAG", "got first failure");

            }
        });
    }

    public static void getMyUserByGoogle(GoogleToken googleToken, final APIUserListener apiUserListener) {

        init();

        Call<LoginInfo> call = aftabeService.getMyUserLogin(googleToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {
                Log.d("TAG", response.toString());
                Log.d("TAG", response.body().accessToken + " " + response.body().created);
                final LoginInfo loginInfo = response.body();

                Call<User> c = aftabeService.getMyUser(response.body().accessToken);
                c.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Response<User> response) {
                        response.body().setLoginInfo(loginInfo);
                        apiUserListener.onGetUser(response.body());

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        apiUserListener.onGetError();
                    }
                });

            }

            @Override
            public void onFailure(Throwable t) {
                apiUserListener.onGetError();
                Log.d("TAG", "Fail");
            }
        });
    }

}

package ir.treeco.aftabe.API;

import android.util.Log;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.okhttp.OkHttpClient;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.API.Utils.GuestCreateToken;
import ir.treeco.aftabe.API.Utils.SMSRequestToken;
import ir.treeco.aftabe.API.Utils.SMSToken;
import ir.treeco.aftabe.Object.TokenHolder;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.API.Utils.LoginInfo;
import ir.treeco.aftabe.Util.Tools;
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

    public static void createGuestUser(final UserLoginListener userLoginListener) {

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

        final GuestCreateToken guestCreateToken = new GuestCreateToken(rand);

        getGuestUser(guestCreateToken, userLoginListener);

    }

    public static void getGuestUser(final GuestCreateToken guestCreateToken, final UserLoginListener userLoginListener) {

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
                        response.body().setOwnerMe();
                        if (userLoginListener != null) userLoginListener.onGetUser(response.body());
                        Tools.updateSharedPrefsToken(response.body(), new TokenHolder(guestCreateToken));

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (userLoginListener != null) userLoginListener.onGetError();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                if (userLoginListener != null) userLoginListener.onGetError();
            }
        });
    }

    public static void getUser(User myUser, String otherUserId, final UserLoginListener userLoginListener) {

        init();

        Call<User> call = aftabeService.getUser(myUser.getLoginInfo().accessToken, otherUserId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if (userLoginListener != null) userLoginListener.onGetUser(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                if (userLoginListener != null) userLoginListener.onGetError();

            }
        });
    }

    public static void getSMSActivatedUser(final SMSToken smsToken, final UserLoginListener userLoginListener) {
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
                        response.body().setOwnerMe();
                        if (userLoginListener != null) userLoginListener.onGetUser(response.body());
                        Tools.updateSharedPrefsToken(response.body(), new TokenHolder(smsToken));

                    }

                    @Override
                    public void onFailure(Throwable t) {

                        if (userLoginListener != null) userLoginListener.onGetError();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                if (userLoginListener != null) userLoginListener.onGetError();
            }
        });
    }

    public static void submitSMSActivation(final SMSToken smsToken, String imei,
                                           String name, String validationCode, final UserLoginListener userLoginListener) {
        init();
        smsToken.update(imei, name, validationCode);
        getSMSActivatedUser(smsToken, userLoginListener);
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

    public static void getMyUserByGoogle(final GoogleToken googleToken, final UserLoginListener userLoginListener) {

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
                        response.body().setOwnerMe();
                        if (userLoginListener != null) userLoginListener.onGetUser(response.body());
                        Tools.updateSharedPrefsToken(response.body(), new TokenHolder(googleToken));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (userLoginListener != null) userLoginListener.onGetError();
                    }
                });

            }

            @Override
            public void onFailure(Throwable t) {
                if (userLoginListener != null) userLoginListener.onGetError();
                Log.d("TAG", "Fail");
            }
        });
    }

    public static void tryToLogin(UserLoginListener userLoginListener) {
        Log.d("AftabeLoginAdapter", "try to login");
        if (!Prefs.contains(Tools.ENCRYPT_KEY) || !Prefs.contains(Tools.SHARED_PREFS_TOKEN))
            return;
        try {
            Gson gson = new Gson();
            TokenHolder tokenHolder = gson.fromJson(Prefs.getString(Tools.SHARED_PREFS_TOKEN, ""), TokenHolder.class);
            if (tokenHolder.getType() == TokenHolder.TOKEN_TYPE_GOOGLE) {
                GoogleToken googleToken = tokenHolder.getGoogleToken();
                getMyUserByGoogle(googleToken, userLoginListener);

            } else if (tokenHolder.getType() == TokenHolder.TOKEN_TYPE_GUEST) {
                GuestCreateToken guestCreateToken = tokenHolder.getGuestCreateToken();
                getGuestUser(guestCreateToken, userLoginListener);
            } else if (tokenHolder.getType() == TokenHolder.TOKEN_TYPE_SMS) {
                SMSToken smsToken = tokenHolder.getSMSToken();
                getSMSActivatedUser(smsToken, userLoginListener);
            }
        } catch (Exception e) {
            Log.d("AftabeLoginAdapter", "exception accoured in try to login ");
            e.printStackTrace();
        }
    }


}

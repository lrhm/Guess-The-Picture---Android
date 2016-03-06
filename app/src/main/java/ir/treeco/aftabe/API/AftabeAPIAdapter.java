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
import ir.treeco.aftabe.API.Utils.UsernameCheck;
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
public class AftabeAPIAdapter {

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

    public static void createGuestUser(final UserFoundListener userFoundListener) {

        init();

        Random random = new Random(System.currentTimeMillis());

        String rand = "";

        for (int i = random.nextInt(79); i < random.nextInt(85); i++) {
            rand = random.nextInt(10000000) + "";
            if (i % 5 == 0)
                break;
        }
        if (rand.compareTo("") == 0)
            rand = random.nextInt(10000000) + "";

        final GuestCreateToken guestCreateToken = new GuestCreateToken(rand.substring(rand.length() - 8));

        getGuestUser(guestCreateToken, userFoundListener);

    }

    public static void getGuestUser(final GuestCreateToken guestCreateToken, final UserFoundListener userFoundListener) {

        Call<LoginInfo> call = aftabeService.getGuestUserLogin(guestCreateToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {
                final LoginInfo loginInfo = response.body();

                getMyUserByAccessToken(loginInfo, userFoundListener);

            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();
            }
        });
    }

    public static void getUser(User myUser, String otherUserId, final UserFoundListener userFoundListener) {

        init();

        Call<User> call = aftabeService.getUser(myUser.getLoginInfo().accessToken, otherUserId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if (userFoundListener != null) userFoundListener.onGetUser(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();

            }
        });
    }

    public static void getSMSActivatedUser(final SMSToken smsToken, final UserFoundListener userFoundListener) {
        Call<LoginInfo> call = aftabeService.getSMSUserLogin(smsToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {

                final LoginInfo loginInfo = response.body();
                getMyUserByAccessToken(loginInfo, userFoundListener);

            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();
            }
        });
    }

    public static void submitSMSActivation(final SMSToken smsToken, String imei,
                                           String name, String validationCode, final UserFoundListener userFoundListener) {
        init();
        smsToken.update(imei, name, validationCode);
        getSMSActivatedUser(smsToken, userFoundListener);
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

    private static void getMyUserByAccessToken(final LoginInfo loginInfo,
                                               final UserFoundListener userFoundListener) {
        Call<User> c = aftabeService.getMyUser(loginInfo.accessToken);
        c.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                response.body().setLoginInfo(loginInfo);
                response.body().setOwnerMe();
                if (userFoundListener != null) userFoundListener.onGetUser(response.body());
                Tools.updateSharedPrefsToken(response.body(), new TokenHolder(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();
            }
        });
    }

    public static void getMyUserByGoogle(final GoogleToken googleToken, final UserFoundListener userFoundListener) {

        init();

        Call<LoginInfo> call = aftabeService.getMyUserLogin(googleToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {
                Log.d("TAG", response.toString());
                Log.d("TAG", response.body().accessToken + " " + response.body().created);
                final LoginInfo loginInfo = response.body();
                getMyUserByAccessToken(loginInfo, userFoundListener);
            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();
                Log.d("TAG", "Fail");
            }
        });
    }

    public static void checkUsername(User myUser, String username, final UsernameCheckListener usernameCheckListener) {

        init();
        Call<UsernameCheck> checkCall = aftabeService.checkUserName(myUser.getLoginInfo().getAccessToken(), username);
        checkCall.enqueue(new Callback<UsernameCheck>() {
            @Override
            public void onResponse(Response<UsernameCheck> response) {
                if (response.body().isUsernameAccessible())
                    usernameCheckListener.onCheckedUsername(true);
                else
                    usernameCheckListener.onCheckedUsername(false);
            }

            @Override
            public void onFailure(Throwable t) {
                usernameCheckListener.onCheckedUsername(false);
            }
        });
    }

    public static void tryToLogin(final UserFoundListener userFoundListener) {

        init();
        Log.d("AftabeAPIAdapter", "try to login");
        if (!Prefs.contains(Tools.ENCRYPT_KEY) || !Prefs.contains(Tools.SHARED_PREFS_TOKEN))
            return;
        try {
            Gson gson = new Gson();
            TokenHolder tokenHolder = gson.fromJson(Prefs.getString(Tools.SHARED_PREFS_TOKEN, ""), TokenHolder.class);
            if (tokenHolder.getLoginInfo() == null)
                return;

            getMyUserByAccessToken(tokenHolder.getLoginInfo(), userFoundListener);

        } catch (Exception e) {
            Log.d("AftabeAPIAdapter", "exception accoured in try to login ");
            e.printStackTrace();
        }
    }

    public static void searchForUser(User myUser, String search, UserFoundListener userFoundListener) {
        if (Tools.isAPhoneNumber(search))
            searchUserByNumber(myUser, (search.length() == 10) ? search : search.substring(1),
                    userFoundListener);
        else if (Tools.isAEmail(search))
            searchUserByMail(myUser, search, userFoundListener);
        else
            searchUserByName(myUser, search, userFoundListener);


    }

    public static void searchUserByNumber(User myUser, String search, final UserFoundListener userFoundListener) {

        Call<User[]> call =
                aftabeService.searchByPhoneNumber(myUser.getLoginInfo().getAccessToken(), search);
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {


                if (response.body().length == 0)
                    userFoundListener.onGetError();

                for (User user : response.body())
                    userFoundListener.onGetUser(user);
            }

            @Override
            public void onFailure(Throwable t) {
                userFoundListener.onGetError();

            }
        });
    }

    public static void searchUserByMail(User myUser, String search, final UserFoundListener userFoundListener) {

        Call<User[]> call =
                aftabeService.searchByEmail(myUser.getLoginInfo().getAccessToken(), search);
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {


                if (response.body().length == 0)
                    userFoundListener.onGetError();

                for (User user : response.body())
                    userFoundListener.onGetUser(user);
            }

            @Override
            public void onFailure(Throwable t) {
                userFoundListener.onGetError();
            }
        });
    }

    public static void searchUserByName(User myUser, String search, final UserFoundListener userFoundListener) {

        Call<User[]> call =
                aftabeService.searchByUsername(myUser.getLoginInfo().getAccessToken(), search);
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {

                if (response.body().length == 0)
                    userFoundListener.onGetError();

                for (User user : response.body())
                    userFoundListener.onGetUser(user);
            }

            @Override
            public void onFailure(Throwable t) {
                userFoundListener.onGetError();
            }
        });
    }


}

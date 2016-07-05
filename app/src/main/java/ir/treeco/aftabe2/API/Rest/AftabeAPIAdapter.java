package ir.treeco.aftabe2.API.Rest;

import android.content.Context;

import ir.treeco.aftabe2.Util.Logger;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import ir.treeco.aftabe2.API.Rest.Interfaces.BatchUserFoundListener;
import ir.treeco.aftabe2.API.Rest.Interfaces.OldUserListener;
import ir.treeco.aftabe2.API.Rest.Interfaces.OnCancelFriendReqListener;
import ir.treeco.aftabe2.API.Rest.Interfaces.OnFriendRequest;
import ir.treeco.aftabe2.API.Rest.Interfaces.OnPackageBuyListener;
import ir.treeco.aftabe2.API.Rest.Interfaces.SMSValidationListener;
import ir.treeco.aftabe2.API.Rest.Interfaces.UserFoundListener;
import ir.treeco.aftabe2.API.Rest.Interfaces.UsernameCheckListener;
import ir.treeco.aftabe2.API.Rest.Utils.AppListHolder;
import ir.treeco.aftabe2.API.Rest.Utils.CoinDiffHolder;
import ir.treeco.aftabe2.API.Rest.Utils.ContactsHolder;
import ir.treeco.aftabe2.API.Rest.Utils.CountHolder;
import ir.treeco.aftabe2.API.Rest.Utils.ForceObject;
import ir.treeco.aftabe2.API.Rest.Utils.FriendRequestSent;
import ir.treeco.aftabe2.API.Rest.Utils.GCMTokenHolder;
import ir.treeco.aftabe2.API.Rest.Utils.GoogleToken;
import ir.treeco.aftabe2.API.Rest.Utils.GuestCreateToken;
import ir.treeco.aftabe2.API.Rest.Utils.IndexHolder;
import ir.treeco.aftabe2.API.Rest.Utils.LeaderboardContainer;
import ir.treeco.aftabe2.API.Rest.Utils.LocationHolder;
import ir.treeco.aftabe2.API.Rest.Utils.SMSCodeHolder;
import ir.treeco.aftabe2.API.Rest.Utils.SMSRequestToken;
import ir.treeco.aftabe2.API.Rest.Utils.SMSToken;
import ir.treeco.aftabe2.API.Rest.Utils.SMSValidateToken;
import ir.treeco.aftabe2.API.Rest.Utils.UsernameCheck;
import ir.treeco.aftabe2.API.Rest.Utils.Veryfier;
import ir.treeco.aftabe2.Adapter.Cache.AppListAdapter;
import ir.treeco.aftabe2.Adapter.Cache.FriendRequestState;
import ir.treeco.aftabe2.Adapter.Cache.PackageSolvedCache;
import ir.treeco.aftabe2.Adapter.CoinAdapter;
import ir.treeco.aftabe2.Adapter.DBAdapter;
import ir.treeco.aftabe2.Adapter.HiddenAdapter;
import ir.treeco.aftabe2.Object.PackageObject;
import ir.treeco.aftabe2.Object.TokenHolder;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.API.Rest.Utils.LoginInfo;
import ir.treeco.aftabe2.Service.RegistrationIntentService;
import ir.treeco.aftabe2.Util.RandomString;
import ir.treeco.aftabe2.Util.Tools;
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
    private static String baseUrl = "https://aftabe2.com:2020";
    private static final String TAG = "AftabeAPIAdapter";
    private static Context context;

    private static final Integer successCode = 200;

    public static boolean isNull() {
        return retrofit == null;
    }

    public static void setContext(Context context) {
        AftabeAPIAdapter.context = context;

    }

    private static void init() {
        if (retrofit == null) {

            baseUrl = Tools.getUrl();

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

        User hdnUser = HiddenAdapter.getInstance().getHiddenUsr();
        if (hdnUser == null) {

            init();

            final GuestCreateToken guestCreateToken = new GuestCreateToken(RandomString.nextString());

            getGuestUser(false, guestCreateToken, userFoundListener);

            return;
        }


//        Tools.updateSharedPrefsToken(context, hdnUser, new TokenHolder(hdnUser));

        PackageSolvedCache.getInstance().updateToServer();


        if (userFoundListener != null) userFoundListener.onGetUser(hdnUser);
        if (userFoundListener != null) userFoundListener.onGetMyUser(hdnUser);


    }

    public static void createHdnUser(final UserFoundListener userFoundListener) {

        init();

        final GuestCreateToken guestCreateToken = new GuestCreateToken(RandomString.nextString());

        getGuestUser(true, guestCreateToken, userFoundListener);

    }


    public static void getGuestUser(final boolean hidden, final GuestCreateToken guestCreateToken, final UserFoundListener userFoundListener) {

        Logger.d(TAG, "getGuestUser");
        Call<LoginInfo> call = aftabeService.getGuestUserLogin(guestCreateToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {


                if (!response.isSuccess() || response.code() != successCode) {
                    Logger.d(TAG, "not getGuestUser succfessful");
                    userFoundListener.onGetError();
                    return;
                }

                final LoginInfo loginInfo = response.body();

                if (!hidden)
                    getMyUserByAccessToken(loginInfo, userFoundListener);
                else
                    getHdnUserByAccessToken(loginInfo, userFoundListener);

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


                if (!response.isSuccess()) {
                    userFoundListener.onGetError();
                    return;
                }

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


                if (!response.isSuccess() || response.code() != successCode) {
                    userFoundListener.onGetError();
                    return;
                }

                final LoginInfo loginInfo = response.body();
                getMyUserByAccessToken(loginInfo, userFoundListener);

            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();
            }
        });
    }

    public static void submitSMSActivationCode(SMSValidateToken smsValidateToken, String userName,
                                               final UserFoundListener userFoundListener) {

        SMSToken smsToken = new SMSToken();
        smsToken.update(smsValidateToken, userName);
        getSMSActivatedUser(smsToken, userFoundListener);
    }

    public static void checkSMSActivationCode(SMSValidateToken smsToken, SMSCodeHolder codeHolder, final SMSValidationListener smsValidationListener) {

        init();

        Call<SMSValidateToken> call = aftabeService.checkSMSCodeReq(codeHolder, smsToken.getId());
        call.enqueue(new Callback<SMSValidateToken>() {
            @Override
            public void onResponse(Response<SMSValidateToken> response) {

                if (response.isSuccess() && response.code() == successCode)
                    smsValidationListener.onValidatedCode(response.body());
                else
                    smsValidationListener.onSMSValidationCodeFail();

            }

            @Override
            public void onFailure(Throwable t) {

                smsValidationListener.onSMSValidationCodeFail();
            }
        });
    }

    public static void requestSMSActivation(SMSRequestToken smsRequestToken, final SMSValidationListener smsValidationListener) {


        init();

        Logger.d(TAG, "request sms activation");
        Call<SMSValidateToken> smsTokenCall = aftabeService.getSMSToken(smsRequestToken);
        smsTokenCall.enqueue(new Callback<SMSValidateToken>() {
            @Override
            public void onResponse(Response<SMSValidateToken> response) {

                if (!response.isSuccess() || response.code() != successCode) {
                    smsValidationListener.onSMSValidationFail();
                } else
                    smsValidationListener.onSMSValidateSent(response.body());

                Logger.d(TAG, "request sms activation on response " + response.isSuccess());
            }

            @Override
            public void onFailure(Throwable t) {
                smsValidationListener.onSMSValidationFail();

            }
        });
    }

    private static void getMyUserByAccessToken(final LoginInfo loginInfo,
                                               final UserFoundListener userFoundListener) {

        Logger.d(TAG, "get user by access token");
        Call<User> c = aftabeService.getMyUser(loginInfo.accessToken);
        c.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {

                if (!response.isSuccess() || response.code() != successCode) {
                    userFoundListener.onGetError();

//                    FORCE LOGOUT !
                    if (response.code() == 401)
                        userFoundListener.onForceLogout();
                    Logger.d(TAG, " is not sucess");
                    return;
                }
                if (response.code() != 200)
                    return;

                CoinAdapter.onUserGet();

                Logger.d(TAG, " is  sucess");
                Logger.d(TAG, (userFoundListener == null) + " is null ?");

                response.body().setLoginInfo(loginInfo);
                response.body().setOwnerMe();

                Logger.d(TAG, new Gson().toJson(response.body()));

                Tools.updateSharedPrefsToken(context, response.body(), new TokenHolder(response.body()));

                PackageSolvedCache.getInstance().updateToServer();

                response.body().setFromServer(true);


                if (userFoundListener != null) userFoundListener.onGetUser(response.body());
                if (userFoundListener != null) userFoundListener.onGetMyUser(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Logger.d(TAG, "failure at get user by access token");
                Logger.d(TAG, t.toString());
                t.printStackTrace();

                if (userFoundListener != null) userFoundListener.onGetError();
            }
        });
    }


    private static void getHdnUserByAccessToken(final LoginInfo loginInfo,
                                                final UserFoundListener userFoundListener) {

        Logger.d(TAG, "get hdn user by access token");
        Call<User> c = aftabeService.getMyUser(loginInfo.accessToken);
        c.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {

                if (response.isSuccess() && response.code() == successCode) {

                    Logger.d(TAG, " is  sucess");
                    Logger.d(TAG, (userFoundListener == null) + " is null ?");

                    response.body().setLoginInfo(loginInfo);
                    response.body().setOwnerMe();

                    HiddenAdapter.getInstance().setHiddenUsr(response.body());


                    if (userFoundListener != null) userFoundListener.onGetUser(response.body());
                    if (userFoundListener != null) userFoundListener.onGetMyUser(response.body());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Logger.d(TAG, "failure at get user by access token");
                Logger.d(TAG, t.toString());
                t.printStackTrace();

                if (userFoundListener != null) userFoundListener.onGetError();
            }
        });
    }


    public static void getMyUserByGoogle(final GoogleToken googleToken, final UserFoundListener userFoundListener) {

        init();

        Logger.d(TAG, new Gson().toJson(googleToken));

        Call<LoginInfo> call = aftabeService.getMyUserLogin(googleToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {
                if (!response.isSuccess() || response.code() != successCode) {
                    userFoundListener.onGetError();

                    return;
                }
                Logger.d(TAG, response.toString());
                Logger.d(TAG, response.body().toString());
                Logger.d(TAG, response.body().accessToken + " " + response.body().created);
                Logger.d(TAG, new Gson().toJson(response.body()));
                final LoginInfo loginInfo = response.body();
                getMyUserByAccessToken(loginInfo, userFoundListener);
            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();
                Logger.d("TAG", "Fail");
            }
        });
    }

    public static void checkUsername(final String username, final UsernameCheckListener usernameCheckListener) {

        init();
        Call<UsernameCheck> checkCall = aftabeService.checkUserName(username);
        checkCall.enqueue(new Callback<UsernameCheck>() {
            @Override
            public void onResponse(Response<UsernameCheck> response) {

                if (!response.isSuccess() || response.code() != successCode) {
                    usernameCheckListener.onCheckedUsername(false, username);
                    return;
                }

                if (response.body().isUsernameAccessible())
                    usernameCheckListener.onCheckedUsername(true, username);
                else
                    usernameCheckListener.onCheckedUsername(false, username);
            }

            @Override
            public void onFailure(Throwable t) {
                usernameCheckListener.onCheckedUsername(false, username);
            }
        });
    }

    public static void tryToLogin(final UserFoundListener userFoundListener) {

        init();
        Logger.d("AftabeAPIAdapter", "try to login");
        if (!Prefs.contains(Tools.ENCRYPT_KEY) || !Prefs.contains(Tools.SHARED_PREFS_TOKEN))
            return;
        try {
            Logger.d("AftabeAPIAdapter", "shared pref have some thing for you");
            Gson gson = new Gson();
            TokenHolder tokenHolder = gson.fromJson(Prefs.getString(Tools.SHARED_PREFS_TOKEN, ""), TokenHolder.class);
            if (tokenHolder == null || tokenHolder.getLoginInfo() == null)
                return;
            Logger.d(TAG, Prefs.getString(Tools.SHARED_PREFS_TOKEN, ""));

            getMyUserByAccessToken(tokenHolder.getLoginInfo(), userFoundListener);

        } catch (Exception e) {
            userFoundListener.onGetError();
            Logger.d("AftabeAPIAdapter", "exception accoured in try to login ");
            e.printStackTrace();
        }
    }

    public static void searchForUser(User myUser, String search, UserFoundListener userFoundListener) {
        if (Tools.isAPhoneNumber(search)) {

            if (search.length() == 11) search = search.substring(1);
            else if (search.length() == 13) search = search.substring(3);
            else if (search.length() == 14) search = search.substring(4);

            Logger.d(TAG, "clear number is " + search);


            searchUserByNumber(myUser, search,
                    userFoundListener);


        } else if (Tools.isAEmail(search))
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


                if (response.body().length == 0 || response.code() != successCode)
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


                if (response.body().length == 0 || response.code() != successCode)
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


                if (response.body() == null || response.code() != successCode) {
                    userFoundListener.onGetError();
                    return;
                }

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

    public static void getLoaderboard(User myUser, final BatchUserFoundListener leaderboardUserListener) {
        init();
        Call<LeaderboardContainer> call = aftabeService.getLeaderboard(myUser.getLoginInfo().getAccessToken(), "score");
        call.enqueue(new Callback<LeaderboardContainer>() {
            @Override
            public void onResponse(Response<LeaderboardContainer> response) {


                if (!response.isSuccess() || response.code() != successCode)
                    leaderboardUserListener.onGotError();
                else
                    leaderboardUserListener.onGotUserList(response.body().getBoard());
            }

            @Override
            public void onFailure(Throwable t) {

                leaderboardUserListener.onGotError();
            }
        });
    }

    private static final Object updateCoinLock = new Object();
    private static boolean isUpdateCoinInProgress = false;

    public static boolean isIsUpdateCoinInProgress() {
        synchronized (updateCoinLock) {
            return isUpdateCoinInProgress;
        }
    }

    public static void updateCoin(User myUser) {

        synchronized (updateCoinLock) {
            if (isUpdateCoinInProgress)
                return;
            isUpdateCoinInProgress = true;
        }


        init();
        final int diff = CoinAdapter.getCoinDiff();

        if (diff == 0) {


            synchronized (updateCoinLock) {
                isUpdateCoinInProgress = false;
            }
            return;
        }

        CoinAdapter.onDiffsent();

        Logger.d(TAG, "coin diff is " + diff);

        DBAdapter dbAdapter = DBAdapter.getInstance(context);
        Logger.d(TAG, dbAdapter.getCoins() + " is db coins");
        Logger.d(TAG, myUser.getCoins() + " is the user coins");

        CoinDiffHolder coinDiffHolder = new CoinDiffHolder(diff);
        Call<User> call = aftabeService.updateCoin(coinDiffHolder, myUser.getId(), myUser.getLoginInfo().getAccessToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if (response.isSuccess() && response.body() != null && response.code() == successCode) {
                    Logger.d(TAG, "new user coin is " + response.body().getCoins());
                    CoinAdapter.onDiffToServer();
                    CoinAdapter.addCoinDiff(-diff);
//                    Prefs.putInt(CoinAdapter.SHARED_PREF_COIN_DIFF, );

                }


                synchronized (updateCoinLock) {
                    isUpdateCoinInProgress = false;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                synchronized (updateCoinLock) {
                    isUpdateCoinInProgress = false;
                }
            }
        });
    }


    public static void updatePckgsList(ArrayList<String> list, final Context mContext) {

        User myUser = Tools.getCachedUser(context);
        if (myUser == null || myUser.getId() == null || myUser.getLoginInfo().getAccessToken() == null)
            myUser = HiddenAdapter.getInstance().getHiddenUsr();
        if (myUser == null || myUser.getId() == null || myUser.getLoginInfo().getAccessToken() == null)
            return;
        init();

        AppListHolder apps = new AppListHolder(list);

        aftabeService.updatePackagesList(apps, myUser.getId(), myUser.getLoginInfo().getAccessToken())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Response<User> response) {

                        if (response.isSuccess() && response.code() == successCode) {
                            AppListAdapter.setUpdateTime(mContext);
                            Logger.d(TAG, "packageList sent");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });


    }

    public static void updateGCMToken(String gcmToken) {

        User myUser = Tools.getCachedUser(context);
        if (myUser == null || myUser.getId() == null || myUser.getLoginInfo().getAccessToken() == null)
            myUser = HiddenAdapter.getInstance().getHiddenUsr();
        if (myUser == null || myUser.getId() == null || myUser.getLoginInfo().getAccessToken() == null)
            return;

        init();

        GCMTokenHolder gcmTokenHolder = new GCMTokenHolder(gcmToken);

        Call<User> call = aftabeService.updateGCMToken(myUser.getId(), myUser.getLoginInfo().getAccessToken(), gcmTokenHolder);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if (response.isSuccess() && response.code() == successCode)
                    if (response.body() != null) {
                        Prefs.putBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, true);
                        return;
                    }
                Prefs.putBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);

            }

            @Override
            public void onFailure(Throwable t) {
                Prefs.putBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);

            }
        });

    }

    public static void requestFriend(User myUser, final String friendId, final OnFriendRequest onFriendRequest) {

        init();


        Call<FriendRequestSent> call = aftabeService.requestFriend(myUser.getId(), friendId, myUser.getLoginInfo().getAccessToken());
        call.enqueue(new Callback<FriendRequestSent>() {
            @Override
            public void onResponse(Response<FriendRequestSent> response) {

                if (response.isSuccess() && response.code() == successCode)
                    if (response.body() != null) {
                        if (onFriendRequest != null) {
                            onFriendRequest.onFriendRequestSent();

                            FriendRequestState.getInstance().friendRequestSend(friendId);

                        }
                        return;
                    }
                if (onFriendRequest != null)
                    onFriendRequest.onFriendRequestFailedToSend();

            }

            @Override
            public void onFailure(Throwable t) {
                if (onFriendRequest != null)
                    onFriendRequest.onFriendRequestFailedToSend();
            }
        });

    }

    public static void getListOfSentFriendRequests(User myUser, final BatchUserFoundListener listener) {
        init();

        Call<User[]> call = aftabeService.getListOfSentFriendRequests(myUser.getId(), myUser.getLoginInfo().getAccessToken());

        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {
                if (response.isSuccess()) {
                    if (response.body() != null && response.body().length != 0) {
                        for (User user : response.body())
                            user.setIsFriend(false);
                        listener.onGotUserList(response.body());
                        return;
                    }
                    return;
                }
                listener.onGotError();

            }

            @Override
            public void onFailure(Throwable t) {

                listener.onGotError();

            }
        });

    }

    public static void cancelFriendRequest(User myUser, String friendId, final OnCancelFriendReqListener listener) {

        init();

        Call<HashMap<String, Object>> call = aftabeService.setCancelFriendRequest(myUser.getId(), friendId, myUser.getLoginInfo().getAccessToken());
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Response<HashMap<String, Object>> response) {
                if (response.isSuccess()) {
                    listener.onSuccess();
                    //TODO maybe change this
                    return;
                }
                listener.onFail();
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onFail();
            }
        });
    }

    public static void getListOfFriendRequestsToMe(User myUser, final BatchUserFoundListener listener) {

        if (myUser == null)
            return;

        init();

        Call<User[]> call = aftabeService.getListOfFriendRequestsToMe(myUser.getLoginInfo().getAccessToken());

        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {
                if (response.isSuccess() && response.code() == successCode) {
                    if (response.body().length != 0) {

                        for (User user : response.body())
                            user.setIsFriend(false);

                        listener.onGotUserList(response.body());
                        return;
                    }
                    return;
                }
                listener.onGotError();
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onGotError();
            }
        });
    }

    public static void getListOfMyFriends(User myUser, final BatchUserFoundListener listener) {

        init();

        Call<User[]> call = aftabeService.getListOfMyFriends(myUser.getId(), myUser.getLoginInfo().getAccessToken());

        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {
                if (response.isSuccess() && response.code() == successCode) {
                    if (response.body().length != 0) {
                        for (User user : response.body()) {
                            Logger.d(TAG, "friend is " + new Gson().toJson(user));

                        }
                    }
                    listener.onGotUserList(response.body());

                    return;
                }
                listener.onGotError();

            }

            @Override
            public void onFailure(Throwable t) {
                listener.onGotError();
            }
        });
    }


    public static void removeFriend(User myUser, String friendId, final OnCancelFriendReqListener listener) {
        init();

        Call<HashMap<String, Object>> call = aftabeService.setRemoveFriend(myUser.getId(), friendId, myUser.getLoginInfo().getAccessToken());

        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Response<HashMap<String, Object>> response) {

                if (response.isSuccess() && response.code() == successCode) {
                    listener.onSuccess();
                    return;
                }
                listener.onFail();

            }

            @Override
            public void onFailure(Throwable t) {
                listener.onFail();
            }
        });
    }

    public static void updateLocation(LocationHolder locationHolder) {
        init();

        if (!Tools.isUserRegistered())
            return;
        User user = Tools.getCachedUser(context);
        if (user == null)
            user = HiddenAdapter.getInstance().getHiddenUsr();
        if (user == null)
            return;

        Call<HashMap<String, Object>> call = aftabeService.putLocation(user.getId(),
                user.getLoginInfo().getAccessToken(), locationHolder);

        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Response<HashMap<String, Object>> response) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public static void updateContact(ContactsHolder contactsHolder, Callback<HashMap<String, String>> callback) {
        init();

        User user = Tools.getCachedUser(context);

        if (user == null)
            user = HiddenAdapter.getInstance().getHiddenUsr();

        if (user == null)
            return;

        Call<HashMap<String, String>> call = aftabeService.putContacts(user.getId(),
                user.getLoginInfo().getAccessToken(), contactsHolder);

        call.enqueue(callback);

    }

    public static void getPackage(int id, Callback<PackageObject> callback) {

        init();

        aftabeService.getPackage(id + "").enqueue(callback);

    }

    public static void getPackageCount(Callback<CountHolder> callback) {

        init();

        aftabeService.getPackagesCount().enqueue(callback);
    }

    public static void getAllPackages(Callback<PackageObject[]> callback) {
        init();

        aftabeService.getAllPackages().enqueue(callback);

    }

    public static void buyPackage(int packageId, final OnPackageBuyListener listener) {
        init();

        User user = Tools.getCachedUser(context);
        if (user == null)
            user = HiddenAdapter.getInstance().getHiddenUsr();
        if (user == null)
            return;

        Logger.d(TAG, "buying package");
        aftabeService.buyPackages(packageId + "", user.getLoginInfo().getAccessToken()).enqueue(new Callback<ArrayList<Integer>>() {
            @Override
            public void onResponse(Response<ArrayList<Integer>> response) {

                if (response.isSuccess() && response.code() == successCode)
                    if (listener != null) listener.onPurchaseSuccess();
                    else if (listener != null) listener.onPurchasedBefore();

                Logger.d(TAG, " buy package response is " + response.message());
            }

            @Override
            public void onFailure(Throwable t) {

                Logger.d(TAG, "fail aon buy package");
                if (listener != null)
                    listener.onFail();

            }
        });


    }

    public static void getCTS(final BatchUserFoundListener listener) {
        init();

        User user = Tools.getCachedUser(context);
        if (user == null)
            return;

        aftabeService.checkCTS(user.getLoginInfo().getAccessToken()).enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {
                if (response.isSuccess() && response.body() != null && response.code() == successCode)
                    listener.onGotUserList(response.body());
                listener.onGotError();
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onGotError();

            }
        });
    }

    public static void getLastVersion(Callback<ForceObject> callback) {

        init();

        aftabeService.checkForceUpdate().enqueue(callback);
    }


    public static void isOldUser(GoogleToken googleToken, final OldUserListener listener) {
        init();

        aftabeService.verifyGCMOldUser(googleToken).enqueue(new Callback<Veryfier>() {
            @Override
            public void onResponse(Response<Veryfier> response) {
                if (response.isSuccess() && response.code() == successCode)
                    if (response.body() != null) {
                        listener.isOldUser(response.body().isOldUser());
                    }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    public static void updatePackageSolved(final int packageId, final int index) {
        init();

        User myUser = Tools.getCachedUser(context);
        if (myUser == null)
            myUser = HiddenAdapter.getInstance().getHiddenUsr();

        if (myUser == null)
            return;

        IndexHolder indexHolder = new IndexHolder(index + 1);
        aftabeService.updateLastLevelSolved(packageId + "", myUser.getLoginInfo().getAccessToken(), indexHolder)
                .enqueue(new Callback<HashMap<String, Object>>() {
                    @Override
                    public void onResponse(Response<HashMap<String, Object>> response) {

                        if (response.isSuccess() && response.code() == successCode) {
                            PackageSolvedCache.getInstance().onPackageIndexSent(packageId);

                            Logger.d(TAG, "updatet package");
                        } else {
                            Logger.d(TAG, "didnt update package package , buy this shit nigga " + packageId + " " + index);
                            try {
                                Logger.d(TAG, response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            buyPackage(packageId, null);

                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                        Logger.d(TAG, "didnt update package package");
                    }
                });
    }


}

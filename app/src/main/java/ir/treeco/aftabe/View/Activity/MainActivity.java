package ir.treeco.aftabe.View.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.BillingWrapper;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.StartCheckoutEvent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;


import ir.tapsell.tapselldevelopersdk.developer.DeveloperCtaInterface;
import ir.tapsell.tapselldevelopersdk.developer.TapsellDeveloperInfo;
import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.Socket.FriendRequestListener;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestSFHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.OnlineFriendStatusHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.API.Socket.SocketFriendMatchListener;
import ir.treeco.aftabe.API.Socket.SocketListener;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.API.Utils.CoinDiffHolder;
import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.Adapter.Cache.FriendRequestState;
import ir.treeco.aftabe.Adapter.Cache.FriendsHolder;
import ir.treeco.aftabe.Adapter.Cache.MatchRequestCache;
import ir.treeco.aftabe.Adapter.Cache.UserActionCache;
import ir.treeco.aftabe.Adapter.CoinAdapter;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.FriendsAdapter;
import ir.treeco.aftabe.Adapter.OnlineOfferAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.StoreItemHolder;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Service.NotifObjects.ActionHolder;
import ir.treeco.aftabe.Service.RegistrationIntentService;
import ir.treeco.aftabe.Service.ServiceConstants;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.NotificationManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Custom.BackgroundDrawable;
import ir.treeco.aftabe.View.Custom.StarView;
import ir.treeco.aftabe.View.Custom.TimerView;
import ir.treeco.aftabe.View.Custom.ToastMaker;
import ir.treeco.aftabe.View.Custom.UserLevelView;
import ir.treeco.aftabe.View.Dialog.ForceUpdateDialog;
import ir.treeco.aftabe.View.Dialog.FriendRequestDialog;
import ir.treeco.aftabe.View.Dialog.LoadingDialog;
import ir.treeco.aftabe.View.Dialog.LoadingForGameResultDialog;
import ir.treeco.aftabe.View.Dialog.MatchRequestDialog;
import ir.treeco.aftabe.View.Dialog.CustomAlertDialog;
import ir.treeco.aftabe.View.Dialog.UsernameChooseDialog;
import ir.treeco.aftabe.View.Fragment.GameFragment;
import ir.treeco.aftabe.View.Fragment.MainFragment;
import ir.treeco.aftabe.View.Fragment.OnlineGameFragment;
import ir.treeco.aftabe.View.Fragment.StoreFragment;


public class MainActivity extends FragmentActivity implements View.OnClickListener,
        BillingProcessor.IBillingHandler, CoinAdapter.CoinsChangedListener,
        GoogleApiClient.OnConnectionFailedListener, UserFoundListener, SocketListener,
        SocketFriendMatchListener, FriendRequestListener, OnlineGameFragment.OnGameEndListener {


    private ArrayList<FriendRequestDialog> mCachedFriendRequestDialogs = new ArrayList<>();
    LoadingDialog loadingDialogMatchReq = null;
    private boolean isInOnlineGame = false;
    private Object matchRqResultLock = new Object();
    public static final String CONTACTS_PERMISSION = "shared_prefs_contacts_permission";
    public static final String CONTACTS_ASKED_PERMISSION_COUNT = "shared_prefs_contacts_permission_asked_count";

    private static final int PERMISSION_REQUEST_CONTACT = 80;
    private DBAdapter db;
    private Tools tools;
    private ImageView cheatButton;
    private ImageView logo;
    private boolean areCheatsVisible = false;
    private int currentLevel;
    private BillingProcessor billingProcessor;
    private TextView digits;
    private CoinAdapter coinAdapter;
    private OnPackagePurchasedListener mOnPackagePurchasedListener;
    private LengthManager lengthManager;
    private ImageManager imageManager;
    private boolean store = false;
    public UserLevelView playerOne;
    public UserLevelView playerTwo;
    public MainFragment mainFragment;
    public TimerView mTimerView;
    public FrameLayout mTimerContainer;
    private ImageView coinBox;
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";
    private User myUser = null;
    private ArrayList<UserFoundListener> mUserFoundListeners;
    private LoadingDialog mLoadingDialog;
    private LinearLayout starContainer;
    private StarView[] starViews;
    public FriendsAdapter mFriendsAdapter;
    private long matchResultTime = 0;
    LoadingForGameResultDialog mLoadingForGameResultDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        coinAdapter = new CoinAdapter(getApplicationContext(), this);

        checkExtras(getIntent().getExtras());


        SocketAdapter.setContext(this);
        SocketAdapter.addSocketListener(this);
        SocketAdapter.addFriendRequestListener(this);
        SocketAdapter.addFriendSocketListener(this);

        initActivity();


        askForContactPermission();


    }


    private void initActivity() {

        SizeManager.initSizes(this);

        mUserFoundListeners = new ArrayList<>();

        tools = new Tools(getApplication());
        lengthManager = ((MainApplication) getApplicationContext()).getLengthManager();
        imageManager = ((MainApplication) getApplicationContext()).getImageManager();

        digits = (TextView) findViewById(R.id.digits);
        cheatButton = (ImageView) findViewById(R.id.cheat_button);
        logo = (ImageView) findViewById(R.id.logo);
        playerOne = (UserLevelView) findViewById(R.id.player1_online_game);
        playerTwo = (UserLevelView) findViewById(R.id.player2_online_game);


        playerOne.setUserNameTextSize(0.85f);
        playerTwo.setUserNameTextSize(0.85f);


        playerOne.setForOnlineGame(true);
        playerTwo.setForOnlineGame(true);

        mTimerView = new TimerView(this);
        mTimerContainer = ((FrameLayout) findViewById(R.id.timer_online));
        mTimerContainer.addView(mTimerView);

        starContainer = (LinearLayout) findViewById(R.id.star_container);
        initStars();

        setUpPlayers();


        cheatButton.setOnClickListener(this);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cheatButton.getLayoutParams();
        layoutParams.leftMargin = (int) (0.724 * lengthManager.getScreenWidth());
        layoutParams.topMargin = (int) (0.07 * lengthManager.getScreenWidth());
        UiUtil.setWidth(cheatButton, lengthManager.getCheatButtonSize());
        UiUtil.setHeight(cheatButton, lengthManager.getCheatButtonSize());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (fragmentManager.getBackStackEntryCount() != 0) throw new IllegalStateException();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mainFragment = new MainFragment();

        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();

        setUpCoinBox();
        setUpHeader();
        setOriginalBackgroundColor();

        billingProcessor = new BillingProcessor(this, this, BillingWrapper.Service.CAFE_BAZAAR);

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();


        String tapsellKey = "rraernffrdhehkkmdtabokdtidjelnbktrnigiqnrgnsmtkjlibkcloprioabedacriasm";

        TapsellDeveloperInfo.getInstance().setDeveloperKey(tapsellKey, this);

//        Intent intent = new Intent(this, RegistrationIntentService.class);
//        startService(intent);

        if (!Prefs.getBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false)) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


    }


    public void initStars() {
        starViews = new StarView[3];
        for (int i = 0; i < 3; i++) {
            starViews[i] = new StarView(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = i == 1 ? 0 : (int) (SizeManager.getScreenHeight() * 0.015);
            lp.bottomMargin = (int) (SizeManager.getScreenHeight() * 0.01);

            starContainer.addView(starViews[i], lp);

        }
        starViews[0].rotate(-30);
        starViews[2].rotate(30);

    }

    public void setStarts(int score) {

        for (StarView starView : starViews)
            starView.setDeActivate();

        ArrayList<Integer> idxs = new ArrayList<>();
        if (score >= 1)
            idxs.add(0);
        if (score >= 2)
            idxs.add(1);
        if (score >= 4)
            idxs.add(2);
        for (Integer integer : idxs)
            starViews[integer].setActive();
    }


    private void setUpPlayers() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) playerOne.getLayoutParams();
        lp.topMargin = (int) ((lengthManager.getHeaderHeight() - playerOne.getRealWidth() * 1.05f) / 2);
        lp.leftMargin = (int) (lengthManager.getScreenWidth() * 0.07);

        RelativeLayout.LayoutParams lpTwo = (RelativeLayout.LayoutParams) playerTwo.getLayoutParams();
        lpTwo.topMargin = (int) ((lengthManager.getHeaderHeight() - playerOne.getRealWidth() * 1.05f) / 2);
        lpTwo.leftMargin = (int) (0.93 * lengthManager.getScreenWidth() - playerOne.getRealWidth());


    }

    public void setOnlineGame(boolean isOnline) {

        Log.d(TAG, "set online game " + isOnline);

        isInOnlineGame = isOnline;

        int onlineViewsVisibility = (isOnline ? View.VISIBLE : View.GONE);
        int headerViewsVisibility = (isOnline ? View.GONE : View.VISIBLE);


        logo.setVisibility(headerViewsVisibility);
        coinBox.setVisibility(headerViewsVisibility);
        digits.setVisibility(headerViewsVisibility);

        playerOne.setVisibility(onlineViewsVisibility);
        playerTwo.setVisibility(onlineViewsVisibility);
        mTimerContainer.setVisibility(onlineViewsVisibility);

    }

    public void setTimer(int time) {
        mTimerView.setTimer(time);
    }

    public void setOnlineGameUser(User op) {

        playerOne.setUserName(myUser.getName());
        playerOne.setUserLevel(myUser.getLevel());
        playerOne.mUser = myUser;

        playerTwo.setUserName(op.getName());
        playerTwo.setUserLevel(op.getLevel());
        playerTwo.mUser = op;

    }

    private void setHeaderVisiblity(boolean visible) {
        int headerViewsVisibility = (!visible ? View.GONE : View.VISIBLE);
        logo.setVisibility(headerViewsVisibility);
        coinBox.setVisibility(headerViewsVisibility);
        digits.setVisibility(headerViewsVisibility);


    }

    public void setOnlineGameVisibilityGone() {

        int headerViewsVisibility = View.GONE;
        playerOne.setVisibility(headerViewsVisibility);
        playerTwo.setVisibility(headerViewsVisibility);
        mTimerContainer.setVisibility(headerViewsVisibility);
    }

    private void setUpCoinBox() {
        coinBox = (ImageView) findViewById(R.id.coin_box);

        int coinBoxWidth = lengthManager.getScreenWidth() * 8 / 20;
        int coinBoxHeight = lengthManager.getHeightWithFixedWidth(R.drawable.coin_box, coinBoxWidth);
        coinBox.setImageBitmap(imageManager.loadImageFromResource(R.drawable.coin_box, coinBoxWidth, coinBoxHeight));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coinBox.getLayoutParams();
        layoutParams.topMargin = lengthManager.getScreenWidth() / 15;
        layoutParams.leftMargin = lengthManager.getScreenWidth() / 50;

        RelativeLayout.LayoutParams digitsLayoutParams = (RelativeLayout.LayoutParams) digits.getLayoutParams();
        digitsLayoutParams.topMargin = lengthManager.getScreenWidth() * 32 / 400;
        digitsLayoutParams.leftMargin = lengthManager.getScreenWidth() * 577 / 3600;
        digitsLayoutParams.width = (int) (0.98 * lengthManager.getScreenWidth() / 5);


        Log.d(TAG, "density dpi is " + coinBoxHeight);

        if (SizeManager.getScreenWidth() < 800)
            digits.setShadowLayer(0.5f, 1, 1, Color.BLACK);
        digits.setTypeface(FontsHolder.getNumeralSansMedium(this));
        digits.setTextSize(TypedValue.COMPLEX_UNIT_PX, coinBoxHeight * 0.475f);

        coinBox.setOnClickListener(this);

        CoinAdapter coinAdapter = new CoinAdapter(getApplicationContext(), this);
        coinAdapter.setCoinsChangedListener(this);
    }

    private void setUpHeader() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                lengthManager.getScreenWidth(),
                lengthManager.getHeaderHeight()
        ));

        logo.setImageBitmap(imageManager.loadImageFromResource(
                R.drawable.header, lengthManager.getScreenWidth(),
                lengthManager.getScreenWidth() / 4
        ));
    }

    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#F3C51C"),//F3c51c
                Color.parseColor("#F3B91A"),
                Color.parseColor("#F4A516")
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cheat_button:
                toggleCheatButton();
                break;

            case R.id.coin_box:
                if (!store) {
                    store = true;
                    StoreFragment storeFragment = new StoreFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, storeFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                break;
        }
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public void setupCheatButton(int id) {
        cheatButton.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        areCheatsVisible = false;
        currentLevel = id;


        Picasso.with(this).load(R.drawable.cheat_button).fit().into(cheatButton);
    }

    public void hideCheatButton() {
        cheatButton.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);
    }

    public void toggleCheatButton() {
        disableCheatButton(false);
        if (!areCheatsVisible) {


            Picasso.with(this).load(R.drawable.next_button).fit().into(cheatButton);
            areCheatsVisible = true;

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof GameFragment)
                ((GameFragment) fragment).showCheats();

        } else {


            Picasso.with(this).load(R.drawable.cheat_button).fit().into(cheatButton);
            areCheatsVisible = false;

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof GameFragment)
                ((GameFragment) fragment).hideCheats();
        }
    }

    public void disableCheatButton(boolean enable) {
        cheatButton.setClickable(enable);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        onActivityResultOfTapsell(requestCode, resultCode, data);


        if (billingProcessor == null || !billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onActivityResultOfTapsell(int requestCode, int resultCode, Intent data) {

        if (requestCode != DeveloperCtaInterface.TAPSELL_DIRECT_ADD_REQUEST_CODE)
            return;


        if (data == null
                || !data.hasExtra(DeveloperCtaInterface.TAPSELL_DIRECT_CONNECTED_RESPONSE)
                || !data.hasExtra(DeveloperCtaInterface.TAPSELL_DIRECT_AVAILABLE_RESPONSE)
                || !data.hasExtra(DeveloperCtaInterface.TAPSELL_DIRECT_AWARD_RESPONSE))
            // User didn’t open ad
            return;


        boolean connected = data.getBooleanExtra(DeveloperCtaInterface.TAPSELL_DIRECT_CONNECTED_RESPONSE, false);
        boolean available = data.getBooleanExtra(DeveloperCtaInterface.TAPSELL_DIRECT_AVAILABLE_RESPONSE, false);
        int award = data.getIntExtra(DeveloperCtaInterface.TAPSELL_DIRECT_AWARD_RESPONSE, -1);
        if (award == 0)
            return;
        if (!connected) {
            // Couldn't connect to server
        } else if (!available) {
            // No such Ad was avaialbe
        } else {
            // user got {award} tomans. pay him!!!!
            coinAdapter.earnCoins(OnlineOfferAdapter.getInstance().isRequestForPlay() ? 100 : 20);
            if (OnlineOfferAdapter.getInstance().isRequestForPlay()) {
                OnlineOfferAdapter.getInstance().useOffer();
            }
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {


        Integer price = StoreItemHolder.getInstnce().getPrice(productId);
        if (price == null)
            price = 500;
        //TODO check price is never null
        Answers.getInstance().logPurchase(new PurchaseEvent()
                .putItemPrice(BigDecimal.valueOf(price))
                .putItemId(productId));

        if (productId.equals(StoreFragment.SKU_VERY_SMALL_COIN))
            coinAdapter.earnCoins(StoreFragment.AMOUNT_VERY_SMALL_COIN);
        else if (productId.equals(StoreFragment.SKU_SMALL_COIN))
            coinAdapter.earnCoins(StoreFragment.AMOUNT_SMALL_COIN);
        else if (productId.equals(StoreFragment.SKU_MEDIUM_COIN))
            coinAdapter.earnCoins(StoreFragment.AMOUNT_MEDIUM_COIN);
        else if (productId.equals(StoreFragment.SKU_BIG_COIN))
            coinAdapter.earnCoins(StoreFragment.AMOUNT_BIG_COIN);
        else if (mOnPackagePurchasedListener != null) {
            mOnPackagePurchasedListener.packagePurchased(productId);
            mOnPackagePurchasedListener = null;
        }
        billingProcessor.consumePurchase(productId);
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.e("IAB", "Got error(" + errorCode + "):", error);
    }

    @Override
    public void onBillingInitialized() {
        Log.v("IAB", "Billing initialized.");
    }

    public void setOnPackagePurchasedListener(OnPackagePurchasedListener onPackagePurchasedListener) {
        mOnPackagePurchasedListener = onPackagePurchasedListener;
    }

    @Override
    public void changed(int newAmount) {
        digits.setText(tools.numeralStringToPersianDigits("" + newAmount));


        if (myUser != null) AftabeAPIAdapter.updateCoin(myUser);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "failed to connect to google", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onGetUser(User user) {
        Log.d(TAG, "got the user successfully " + (new Gson()).toJson(user));

        for (UserFoundListener userFoundListener : mUserFoundListeners)
            userFoundListener.onGetUser(user);

    }

    @Override
    public void onGetError() {
        Log.d(TAG, "didnet get the user");

        for (UserFoundListener userFoundListener : mUserFoundListeners)
            userFoundListener.onGetError();
    }

    @Override
    public void onGetMyUser(User mUser) {

        Log.d("TAG", "on get my user main");


        this.myUser = mUser;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UserFoundListener userFoundListener : mUserFoundListeners)
                    userFoundListener.onGetMyUser(MainActivity.this.myUser);

            }
        });

//        Log.d(TAG, "my user coins " + mUser.getCoins());
//        Log.d(TAG, "adapter coin " + coinAdapter.getCoinsCount());
//        Log.d(TAG, "coin diff" + coinAdapter.getCoinDiff());
//        if (mUser.getCoins() + coinAdapter.getCoinDiff() != coinAdapter.getCoinsCount()) {
//            coinAdapter.setCoinsCount(mUser.getCoins());
//        }

    }

    @Override
    public void onForceLogout() {

        Prefs.remove(Tools.SHARED_PREFS_TOKEN);
        Prefs.remove(Tools.USER_SAVED_DATA);
        Prefs.remove(Tools.USER_SAVED_DATA);

    }


    @Override
    public void onMatchRequest(final MatchRequestSFHolder request) {

        if (coinAdapter.getCoinsCount() < 100) {
            SocketAdapter.responseToMatchRequest(request.getFriend().getId(), false);

            return;
        }

        if (!isInOnlineGame && !isFinishing()) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!isFinishing()) {


                        MatchRequestDialog dialog = new MatchRequestDialog(MainActivity.this, request.getFriend());
                        MatchRequestCache.getInstance().add(dialog);
                        dialog.show();
                    }
                }
            });

        }
    }

    @Override
    public void onOnlineFriendStatus(OnlineFriendStatusHolder status) {

    }

    @Override
    public void onMatchResultToSender(MatchResultHolder result) {
        loadingDialogMatchReq = null;
        matchResultTime = System.currentTimeMillis();

        Log.d(TAG, "result is " + new Gson().toJson(result));

        Answers.getInstance().logCustom(new CustomEvent("Match Request Result")
                .putCustomAttribute("status", result.getStatus()));

        if (!result.isAccept()) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {

                        coinAdapter.earnCoins(100);

                    }
                }
            });
        }

        if (result.isAccept()) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    if (!isFinishing()) {
                        if (!isFinishing())

                            synchronized (matchRqResultLock) {


                                if (loadingDialogMatchReq == null) {
                                    loadingDialogMatchReq = new LoadingDialog(MainActivity.this);
                                    loadingDialogMatchReq.show();
                                }
                            }
                    }

                }
            });
        }
    }

    @Override
    public void onFriendRequest(final User user) {

        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()) {
                    FriendRequestDialog dialog = new FriendRequestDialog(MainActivity.this, user);
                    if (!isInOnlineGame && !isFinishing())
                        dialog.show();
                    else
                        mCachedFriendRequestDialogs.add(dialog);
                }
            }
        });


    }

    @Override
    public void onFriendRequestReject(User user) {

        FriendRequestState.getInstance().friendRequestEvent(user, true);
    }

    @Override
    public void onFriendRequestAccept(final User user) {

        FriendRequestState.getInstance().friendRequestEvent(user, false);

        if (!user.isFriend()) {
            user.setIsFriend(true);
        }

        FriendsHolder friendsHolder = FriendsHolder.getInstance();
        friendsHolder.addFriendToList(user);

        if (!isFinishing())
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mFriendsAdapter.addUser(user, FriendsAdapter.TYPE_FRIEND);

                }
            });
    }

    @Override
    public void onGameEnded() {

        for (FriendRequestDialog dialog : mCachedFriendRequestDialogs) {
            if (!isFinishing())
                dialog.show();
        }
        mCachedFriendRequestDialogs.clear();

    }

    public boolean isPaused() {
        return isPaused;
    }

    public interface OnPackagePurchasedListener {
        void packagePurchased(String sku);

    }


    public void purchase(String sku, int price) {
        if (billingProcessor.isInitialized()) {
            Answers.getInstance().logStartCheckout(new StartCheckoutEvent()
                    .putTotalPrice(BigDecimal.valueOf(price))
                    .putCustomAttribute("sku", sku));


            billingProcessor.purchase(sku);
        } else {
            ToastMaker.show(this, "در حال برقراری ارتباط با کافه بازار، کمی دیگر تلاش کنید.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();

        super.onDestroy();
    }

    public void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void handleSignInResult(GoogleSignInResult result) {
        String TAG = "GoogleSignInResult";
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            GoogleToken googleToken = new GoogleToken(acct.getIdToken());

            new UsernameChooseDialog(this, googleToken, this).show();


        } else {
        }
    }

    public User getMyUser() {
        return myUser;
    }

    public void addUserFoundListener(UserFoundListener userFoundListener) {
        mUserFoundListeners.add(userFoundListener);
    }

    @Override
    public void onGotGame(final GameResultHolder gameHolder) {

        if (System.currentTimeMillis() - matchResultTime < 1000)
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        synchronized (matchRqResultLock) {


                            if (loadingDialogMatchReq != null) {
                                loadingDialogMatchReq.showGame(gameHolder);
                            } else {
                                loadingDialogMatchReq = new LoadingDialog(MainActivity.this);
                                loadingDialogMatchReq.show();
                                loadingDialogMatchReq.onGotGame(gameHolder);
                            }
                        }

                    }
                }
            });

    }

    @Override
    public void onGameStart(GameStartObject gameStartObject) {

    }

    @Override
    public void onGotUserAction(final UserActionHolder actionHolder) {


        Log.d(TAG, "got user action");
        if (!actionHolder.getUserId().equals(Tools.getCachedUser().getId())) {
            UserActionCache.getInstance().addToOpponentList(actionHolder.getAction());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerTwo.setOnlineState(actionHolder.getAction());

                }
            });

        }

    }

    @Override
    public void onFinishGame(ResultHolder resultHolder) {

        if (resultHolder.getMyScoreResult(myUser) != 0) {

            myUser.setScore(myUser.getScore() + resultHolder.getMyScoreResult(myUser));
            onGetMyUser(myUser);

            int coin = 160;
            if (resultHolder.getScores()[0].isWinner() && resultHolder.getScores()[1].isWinner()) {
                // draw
                coin = 80;
            }
            if (!resultHolder.amIWinner(myUser))
                coin = 0;
            if (coin != 0)
                coinAdapter.earnCoins(coin);

        }

//        if(resultHolder.getStatus().)

    }


    public void requestRandomGame() {

        if (myUser == null) {
            Toast.makeText(this, "not connected , try again ", Toast.LENGTH_SHORT).show();
            AftabeAPIAdapter.tryToLogin(this);
            return;
        }

        if (!coinAdapter.spendCoins(100)) {


            return;
        }

        playerOne.setOnlineStateClear();
        playerTwo.setOnlineStateClear();


        mLoadingDialog = new LoadingDialog(this, true);

        mLoadingDialog.show();
        SocketAdapter.requestGame();

    }


    public void setGameResult(boolean doSet) {

        int visibily = (doSet) ? View.VISIBLE : View.GONE;

        setHeaderVisiblity(!doSet);
        starContainer.setVisibility(visibily);
        if (doSet)
            setOnlineGameVisibilityGone();
    }

    public void setLoadingForGameResultDialog(LoadingForGameResultDialog loadingForGameResultDialog) {
        mLoadingForGameResultDialog = loadingForGameResultDialog;
    }


    @Override
    public void onBackPressed() {


        final OnlineGameFragment fragment = (OnlineGameFragment) getSupportFragmentManager().findFragmentByTag("FRAGMENT_ONLINE_GAME");
        if (fragment == null) {
            super.onBackPressed();
            return;
        }

        new CustomAlertDialog(this, "بازی تمام خواهد شد . \n ایا مطمپن هستین ؟", "اره", new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragment.doLose();
                MainActivity.this.setOnlineGame(false);
                MainActivity.this.getSupportFragmentManager().popBackStack();
            }
        }, "نه", null).show();
    }


    private boolean isPaused = false;

    @Override
    protected void onPause() {
        if (mLoadingForGameResultDialog != null)
            mLoadingForGameResultDialog.dismiss();

        if (mLoadingDialog != null)
            mLoadingDialog.onBackPressed();

        SocketAdapter.disconnect();

        isPaused = true;

        super.onPause();
    }

    @Override
    protected void onResume() {


        isPaused = false;
        SocketAdapter.reconnect();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isPaused)
                    SocketAdapter.requestOnlineFriendsStatus();

            }
        }, 3000);

        super.onResume();

        AftabeAPIAdapter.tryToLogin(this);


        Log.d(TAG, "super.onResume ended");
    }

    public void askForContactPermission() {

        int tryed = Prefs.getInt(CONTACTS_ASKED_PERMISSION_COUNT, 0);
        if (tryed > 2)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("دست رسی مخاطبان")
                        .setMessage("برای پیدا کردن دوستان و ثبت اطلاعات کاربری ")
                        .setPositiveButton("باشه", new DialogInterface.OnClickListener() {

                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermissions(
                                        new String[]
                                                {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}
                                        , PERMISSION_REQUEST_CONTACT);

                            }
                        }).setNegativeButton("نه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int tryed = Prefs.getInt(CONTACTS_ASKED_PERMISSION_COUNT, 0);
                        Prefs.putInt(CONTACTS_ASKED_PERMISSION_COUNT, tryed + 1);
                    }
                });
                builder.create().show();
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.


            } else {
            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Prefs.putBoolean(CONTACTS_PERMISSION, true);


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                }
                if (grantResults.length > 0) {
                    Tools.checkKey();
                    tools.checkDB();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void setFriendsAdapter(FriendsAdapter mFriendsAdapter) {
        this.mFriendsAdapter = mFriendsAdapter;
    }

    public void setIsInOnlineGame(boolean isInOnlineGame) {
        this.isInOnlineGame = isInOnlineGame;
    }


    public void checkExtras(Bundle bundle) {

        if (bundle == null)
            return;

        String data = bundle.getString(ServiceConstants.ACTION_DATA_INTENT);


        if (data == null)
            return;

        ActionHolder actionHolder = new Gson().fromJson(data, ActionHolder.class);

        NotificationManager.dismissNotification(this, actionHolder.getNotificationID());


        if (actionHolder.isFriendRequest()) {
//            TODO
            new FriendRequestDialog(this, actionHolder.getNotifHolder().getFriendSF().getUser()).show();
            return;
        }
        if (actionHolder.isMatchRequest()) {
            if (actionHolder.isActionSpecified()) {

                if (!coinAdapter.spendCoins(100)) {

                    SocketAdapter.responseToMatchRequest(actionHolder.getNotifHolder().getMatchSF().getFriendId(), false);
                    return;
                }

                SocketAdapter.responseToMatchRequest(actionHolder.getNotifHolder().getMatchSF().getFriendId(), true);
                new LoadingDialog(this).show();
            } else {

                new MatchRequestDialog(this, actionHolder.getNotifHolder().getMatchSF().getFriend()).show();
            }
        }


    }


    public CoinAdapter getCoinAdapter() {
        return coinAdapter;
    }


}

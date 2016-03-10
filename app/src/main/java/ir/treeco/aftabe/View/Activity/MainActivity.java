package ir.treeco.aftabe.View.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.BillingWrapper;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ir.tapsell.tapselldevelopersdk.developer.DeveloperCtaInterface;
import ir.tapsell.tapselldevelopersdk.developer.TapsellDeveloperInfo;
import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.Adapter.CoinAdapter;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.HeadObject;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Custom.BackgroundDrawable;
import ir.treeco.aftabe.View.Custom.ToastMaker;
import ir.treeco.aftabe.View.Custom.UserLevelView;
import ir.treeco.aftabe.View.Dialog.UsernameChooseDialog;
import ir.treeco.aftabe.View.Fragment.GameFragment;
import ir.treeco.aftabe.View.Fragment.MainFragment;
import ir.treeco.aftabe.View.Fragment.StoreFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener,
        BillingProcessor.IBillingHandler, CoinAdapter.CoinsChangedListener,
        GoogleApiClient.OnConnectionFailedListener, UserFoundListener {


    private HeadObject headObject;
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
    private UserLevelView playerOne;
    private UserLevelView playerTwo;
    public MainFragment mainFragment;
    public TextView timerTextView;
    private ImageView coinBox;
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";
    private User myUser = null;
    private ArrayList<UserFoundListener> mUserFoundListeners;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initActivity();


    }


    private void initActivity() {
        mUserFoundListeners = new ArrayList<>();

        tools = new Tools(getApplication());
        coinAdapter = new CoinAdapter(getApplicationContext());
        lengthManager = ((MainApplication) getApplicationContext()).getLengthManager();
        imageManager = ((MainApplication) getApplicationContext()).getImageManager();

        digits = (TextView) findViewById(R.id.digits);
        cheatButton = (ImageView) findViewById(R.id.cheat_button);
        logo = (ImageView) findViewById(R.id.logo);
        playerOne = (UserLevelView) findViewById(R.id.player1_online_game);
        playerTwo = (UserLevelView) findViewById(R.id.player2_online_game);
        timerTextView = (TextView) findViewById(R.id.timer_online);

        setUpPlayers();

        cheatButton.setOnClickListener(this);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cheatButton.getLayoutParams();
        layoutParams.leftMargin = (int) (0.724 * lengthManager.getScreenWidth());
        layoutParams.topMargin = (int) (0.07 * lengthManager.getScreenWidth());

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
        initSizes();
        billingProcessor = new BillingProcessor(this, this, BillingWrapper.Service.IRAN_APPS);

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

        AftabeAPIAdapter.tryToLogin(this);
    }


    public void initSizes() {
        int screenWidth = 0;
        int screenHeight = 0;
        if (Build.VERSION.SDK_INT >= 11) {
            Point size = new Point();
            try {
                // this.getWindowManager().getDefaultDisplay().getRealSize(size);

                this.getWindowManager().getDefaultDisplay().getSize(size);
                screenWidth = size.x;
                screenHeight = size.y;
            } catch (NoSuchMethodError e) {

                DisplayMetrics metrics = new DisplayMetrics();
                this.getWindowManager().getDefaultDisplay()
                        .getRealMetrics(metrics);
                screenWidth = metrics.widthPixels;
                screenHeight = metrics.heightPixels;

            }

        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
        }
        SizeManager.setScreenHeight(screenHeight);
        SizeManager.setScreenWidth(screenWidth);

    }

    private void setUpPlayers() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) playerOne.getLayoutParams();
        lp.topMargin = (int) (lengthManager.getScreenWidth() / 15f);
        lp.leftMargin = (int) (lengthManager.getScreenWidth() * 0.07);

        RelativeLayout.LayoutParams lpTwo = (RelativeLayout.LayoutParams) playerTwo.getLayoutParams();
        lpTwo.topMargin = (int) (lengthManager.getScreenWidth() / 15f);
        lpTwo.leftMargin = (int) (0.8 * lengthManager.getScreenWidth());
    }

    public void setOnlineGame(boolean isOnline) {
        int onlineViewsVisibility = (isOnline ? View.VISIBLE : View.GONE);
        int headerViewsVisibility = (isOnline ? View.GONE : View.VISIBLE);
        logo.setVisibility(headerViewsVisibility);
        coinBox.setVisibility(headerViewsVisibility);
        digits.setVisibility(headerViewsVisibility);

        playerOne.setVisibility(onlineViewsVisibility);
        playerTwo.setVisibility(onlineViewsVisibility);
        timerTextView.setVisibility(onlineViewsVisibility);

    }

    public void setHeaderVisiblity(boolean visible) {
        int headerViewsVisibility = (!visible ? View.GONE : View.VISIBLE);
        logo.setVisibility(headerViewsVisibility);
        coinBox.setVisibility(headerViewsVisibility);
        digits.setVisibility(headerViewsVisibility);


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
        digitsLayoutParams.topMargin = lengthManager.getScreenWidth() * 34 / 400;
        digitsLayoutParams.leftMargin = lengthManager.getScreenWidth() * 577 / 3600;
        digitsLayoutParams.width = lengthManager.getScreenWidth() / 5;

        digits.setTypeface(FontsHolder.getHoma(this));

        coinBox.setOnClickListener(this);

        CoinAdapter coinAdapter = new CoinAdapter(getApplicationContext());
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

        String cheatImagePath = "file://" + getFilesDir().getPath() + "/Downloaded/"
                + id + "_cheatBitmap.png";

        Picasso.with(this).load(cheatImagePath).into(cheatButton);
    }

    public void hideCheatButton() {
        cheatButton.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);
    }

    public void toggleCheatButton() {
        disableCheatButton(false);
        if (!areCheatsVisible) {
            String cheatImagePath = "file://" + getFilesDir().getPath() + "/Downloaded/"
                    + currentLevel + "_backBitmap.png";

            Picasso.with(this).load(cheatImagePath).into(cheatButton);
            areCheatsVisible = true;

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof GameFragment)
                ((GameFragment) fragment).showCheats();

        } else {
            String cheatImagePath = "file://" + getFilesDir().getPath() + "/Downloaded/"
                    + currentLevel + "_cheatBitmap.png";

            Picasso.with(this).load(cheatImagePath).into(cheatButton);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DeveloperCtaInterface.TAPSELL_DIRECT_ADD_REQUEST_CODE) {
            if (data == null
                    || !data.hasExtra(DeveloperCtaInterface.TAPSELL_DIRECT_CONNECTED_RESPONSE)
                    || !data.hasExtra(DeveloperCtaInterface.TAPSELL_DIRECT_AVAILABLE_RESPONSE)
                    || !data.hasExtra(DeveloperCtaInterface.TAPSELL_DIRECT_AWARD_RESPONSE)) {
                // User didn’t open ad
                return;
            }

            boolean connected = data.getBooleanExtra(DeveloperCtaInterface.TAPSELL_DIRECT_CONNECTED_RESPONSE, false);
            boolean available = data.getBooleanExtra(DeveloperCtaInterface.TAPSELL_DIRECT_AVAILABLE_RESPONSE, false);
            int award = data.getIntExtra(DeveloperCtaInterface.TAPSELL_DIRECT_AWARD_RESPONSE, -1);
            if (!connected) {
                // Couldn't connect to server
            } else if (!available) {
                // No such Ad was avaialbe
            } else {
                // user got {award} tomans. pay him!!!!
                coinAdapter.earnCoins(20);
            }
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
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
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "failed to connect to google", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetUser(User user) {
        Log.d(TAG, "got the user successfully " + (new Gson()).toJson(user));
        if (user.isMe()) {
            myUser = user;
            Gson gson = new Gson();
            Prefs.putString(Tools.USER_SAVED_DATA, gson.toJson(myUser));
        }
        for (UserFoundListener userFoundListener : mUserFoundListeners)
            userFoundListener.onGetUser(user);

    }

    @Override
    public void onGetError() {
        Log.d(TAG, "didnet get the user");

        for (UserFoundListener userFoundListener : mUserFoundListeners)
            userFoundListener.onGetError();
    }

    public interface OnPackagePurchasedListener {
        void packagePurchased(String sku);
    }


    public void purchase(String sku) {
        if (billingProcessor.isInitialized())
            billingProcessor.purchase(sku);
        else {
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


}

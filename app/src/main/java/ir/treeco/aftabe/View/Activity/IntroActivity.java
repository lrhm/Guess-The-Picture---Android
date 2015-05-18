package ir.treeco.aftabe.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.BillingWrapper;
import com.anjlab.android.iab.v3.TransactionDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ir.treeco.aftabe.BackgroundDrawable;
import ir.treeco.aftabe.CoinManager;
import ir.treeco.aftabe.View.Fragment.PackageListFragment;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Fragment.StoreFragment;
import ir.treeco.aftabe.View.Toast.ToastMaker;
import ir.treeco.aftabe.synchronization.Synchronizer;
import ir.treeco.aftabe.utils.Encryption;
import ir.treeco.aftabe.utils.FontsHolder;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.LoadingManager;
import ir.treeco.aftabe.utils.SomeTaskStartedListener;
import ir.treeco.aftabe.utils.TaskCallback;
import ir.treeco.aftabe.utils.TaskStartedListener;
import ir.treeco.aftabe.utils.TasksFinishedListener;
import ir.treeco.aftabe.utils.Utils;

public class IntroActivity extends FragmentActivity implements BillingProcessor.IBillingHandler {
    private static final String TAG = "IntroActivity";
    private SharedPreferences preferences;
    private BillingProcessor billingProcessor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make volume control available the entire time
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        // Initialize Height Manager
        LengthManager.initialize(IntroActivity.this);

        // Load Layout
        setContentView(R.layout.activity_intro);

        preferences = getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        updateGameData();

        mainView = (FrameLayout) findViewById(R.id.main_view);

        Utils.updateLastTime(this);

        loadPackagesFragment();
        setUpHeader();
        setUpCoinBox();
        setOriginalBackgroundColor();
        registerLoadingView();
        backupSharedPreferences();

        billingProcessor = new BillingProcessor(this, this, BillingWrapper.Service.IRAN_APPS);
//
        callSynchronizer();
    }

    private void callSynchronizer() {
        new Synchronizer().onReceive(getApplicationContext(), null);
    }

    private void setUpHeader() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        header.setLayoutParams(new LinearLayout.LayoutParams(LengthManager.getScreenWidth(), LengthManager.getHeaderHeight()));

        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.header, LengthManager.getScreenWidth(), LengthManager.getScreenWidth() / 4));

    }

    private void loadPackagesFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (fragmentManager.getBackStackEntryCount() != 0) throw new IllegalStateException();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PackageListFragment listFragment = PackageListFragment.newInstance();
        fragmentTransaction.replace(R.id.fragment_container, listFragment);
        fragmentTransaction.commit();
    }

    private void backupSharedPreferences() {
//        Log.d("paspas",Utils.getAESkey(this));
//        Log.d("paspas",Utils.getAESkey(this).getBytes().length+" ");

        // Backup
        if (Utils.isExternalStorageWritable()) {
            File rootFolder = new File(Environment.getExternalStorageDirectory(), ".aftabe");
            rootFolder.mkdir();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Coin Count", CoinManager.getCoinsCount(preferences));
                Log.i("Backup", jsonObject.toString());
                Log.i("Backup", Encryption.encryptAES(jsonObject.toString(), this));
                FileOutputStream fileOutputStream = new FileOutputStream(new File(rootFolder, "Backup"));
                fileOutputStream.write(Encryption.encryptAES(jsonObject.toString(), this).getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerLoadingView() {
        final RelativeLayout loadingView = (RelativeLayout) getLayoutInflater().inflate(R.layout.view_ehem, null);

        LoadingManager.setSomeTaskStartedListener(new SomeTaskStartedListener() {
            @Override
            public void someTaskStarted(final TaskCallback callback) {
                pushToViewStack(loadingView, false);

                ImageView ehem = (ImageView) loadingView.findViewById(R.id.ehem);
                ehem.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.load, LengthManager.getLoadingEhemWidth(), -1));
                ehem.getLayoutParams().width = LengthManager.getLoadingEhemWidth();
                ehem.setPadding(0, LengthManager.getLoadingEhemPadding(), 0, 0);

                ImageView toilet = (ImageView) loadingView.findViewById(R.id.toilet);
                toilet.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.toilet, LengthManager.getLoadingToiletWidth(), -1));
                toilet.getLayoutParams().width = LengthManager.getLoadingToiletWidth();

                TextView tip = (TextView) loadingView.findViewById(R.id.tip);
                tip.setText(Utils.getRandomTip());
                tip.setTypeface(FontsHolder.getYekan(IntroActivity.this));
                tip.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getTipTextSize());
                tip.setTextColor(Color.WHITE);
                tip.getLayoutParams().width = LengthManager.getTipWidth();

                loadingView.setVisibility(View.VISIBLE);

                final Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(600);
                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loadingView.clearAnimation();

                        callback.done();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                loadingView.clearAnimation();
                loadingView.startAnimation(fadeIn);
            }
        });

        LoadingManager.setTasksFinishedListener(new TasksFinishedListener() {
            @Override
            public void tasksFinished() {
                final Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setStartOffset(200);
                fadeOut.setDuration(200);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loadingView.setVisibility(View.INVISIBLE);
                        loadingView.clearAnimation();

                        popFromViewStack(loadingView);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                loadingView.clearAnimation();
                loadingView.startAnimation(fadeOut);

                int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

                if (backStackEntryCount == 0) {
                    setOriginalBackgroundColor();
                }

                if (backStackEntryCount == 1) {
                    findViewById(R.id.logo).setVisibility(View.VISIBLE);
                    findViewById(R.id.cheat_button).setVisibility(View.GONE);
                    findViewById(R.id.cheat_button).setOnClickListener(null);
                }
            }
        });
    }

    private void setUpCoinBox() {
        ImageView coinBox = (ImageView) findViewById(R.id.coin_box);
        int coinBoxWidth = LengthManager.getScreenWidth() * 9 / 20;
        int coinBoxHeight = LengthManager.getHeightWithFixedWidth(R.drawable.coin_box, coinBoxWidth);
        coinBox.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.coin_box, coinBoxWidth, coinBoxHeight));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coinBox.getLayoutParams();
        layoutParams.topMargin = LengthManager.getScreenWidth() / 15;
        layoutParams.leftMargin = LengthManager.getScreenWidth() / 50;

        LinearLayout digits = (LinearLayout) findViewById(R.id.digits);
        RelativeLayout.LayoutParams digitsLayoutParams = (RelativeLayout.LayoutParams) digits.getLayoutParams();
        digitsLayoutParams.topMargin = LengthManager.getScreenWidth() * 40 / 360;
        digitsLayoutParams.leftMargin = LengthManager.getScreenWidth() * 575 / 3600;
        digitsLayoutParams.width = LengthManager.getScreenWidth() / 5;

        coinBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StoreFragment.getIsUsed())
                    return;

                LoadingManager.startTask(new TaskStartedListener() {
                    @Override
                    public void taskStarted() {
                        StoreFragment fragment = StoreFragment.getInstance();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            }
        });


        CoinManager.setCoinsChangedListener(new CoinManager.CoinsChangedListener() {
            @Override
            public void changed(int newAmount) {
                LinearLayout digits = (LinearLayout) findViewById(R.id.digits);
                digits.removeAllViews();

                String number = "" + CoinManager.getCoinsCount(preferences);

                int[] digitResource = new int[]{
                        R.drawable.digit_0,
                        R.drawable.digit_1,
                        R.drawable.digit_2,
                        R.drawable.digit_3,
                        R.drawable.digit_4,
                        R.drawable.digit_5,
                        R.drawable.digit_6,
                        R.drawable.digit_7,
                        R.drawable.digit_8,
                        R.drawable.digit_9,
                };

                digits.addView(Utils.makeNewSpace(IntroActivity.this));
                for (int i = 0; i < number.length(); i++) {
                    int d = number.charAt(i) - '0';
                    ImageView digit = new ImageView(IntroActivity.this);
                    int digitHeight = LengthManager.getScreenWidth() / 21;
                    digit.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, digitResource[d], LengthManager.getWidthWithFixedHeight(digitResource[d], digitHeight), digitHeight));
                    digits.addView(digit);
                }
                digits.addView(Utils.makeNewSpace(IntroActivity.this));
            }
        }, preferences);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingProcessor == null || !billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    FrameLayout mainView;

    @Override
    public void onBackPressed() {
        if (!currentlyPushedViews.isEmpty()) {
            View view = currentlyPushedViews.get(currentlyPushedViews.size() - 1);
            popFromViewStack(view);
            return;
        }

        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackEntryCount == 0) {
            super.onBackPressed();
            return;
        }

        LoadingManager.startTask(new TaskStartedListener() {
            @Override
            public void taskStarted() {
                IntroActivity.super.onBackPressed();
            }
        });
    }

    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#F3C81D"),
                Color.parseColor("#F3C01E"),
                Color.parseColor("#F49C14")
        }));
    }

    ArrayList<View> currentlyPushedViews = new ArrayList<View>();

    public void pushToViewStack(View view, boolean popOnBackPressed) {
        mainView.addView(view);
        if (popOnBackPressed)
            currentlyPushedViews.add(view);
    }

    public void popFromViewStack(View view) {
        currentlyPushedViews.remove(view);
        mainView.removeView(view);
    }

    final String DATA_VERSION_TAG = "data_version";

    private void updateGameData() {
        updateToVersion1();
        updateToVersion2();
        updateToVersion3();
        updateToVersion4();
    }

    String getGameData() {
        return preferences.getString(DATA_VERSION_TAG, "0");
    }

    private void updateToVersion1() {
        if (!getGameData().equals("0"))
            return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("data_version", "1");
        editor.commit();
    }

    private void updateToVersion2() {
        if (!getGameData().equals("1"))
            return;
        SharedPreferences.Editor editor = preferences.edit();


        for (int i = 0; preferences.getBoolean("level_" + i + "_is_solved", false); i++) {
            editor.remove("level_" + i + "_alphabet_gone");
            editor.remove("level_" + i + "_place_holder");
        }

        editor.putString("data_version", "2");
        editor.commit();
    }

    private void updateToVersion3() {
        if (!getGameData().equals("2"))
            return;
        SharedPreferences.Editor editor = preferences.edit();

        CoinManager.earnCoins(199, preferences);

        editor.putString("data_version", "3");
        editor.commit();
    }

    private void updateToVersion4() {
        if (!getGameData().equals("3"))
            return;
        SharedPreferences.Editor editor = preferences.edit();

        int levelCounts[] = new int[] {96, 96, 112};
        int totalLevelCount = 304;
        for (int i = 0; i < totalLevelCount; i++) {
            int currentPackage = 0;
            int currentLevel = i;
            for (int amount: levelCounts)
                if (amount <= currentLevel) {
                    currentLevel -= amount;
                    currentPackage++;
                }

            {
                String fromKey = "level_" + i + "_is_solved";
                if (preferences.getBoolean(fromKey, false)) {
                    editor.remove(fromKey);
                    editor.putBoolean("package_" + currentPackage + "_level_" + currentLevel + "_is_solved", true);
                }
            }

            renameString(preferences, editor, "level_" + i + "_alphabet_gone", "package_" + currentPackage + "_level_" + currentLevel + "_alphabet_gone");
            renameString(preferences, editor, "level_" + i + "_place_holder", "package_" + currentPackage + "_level_" + currentLevel + "_place_holder");
        }

        editor.putString("data_version", "4");
        editor.commit();
    }

    private void renameString(SharedPreferences preferences, SharedPreferences.Editor editor, String fromKey, String toKey) {
        String value = preferences.getString(fromKey, null);
        if (value == null)
            return;
        editor.remove(fromKey);
        editor.putString(toKey, value);
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails transactionDetails) {
        SharedPreferences preferences = getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        if (productId.equals(StoreFragment.SKU_VERY_SMALL_COIN)) CoinManager.earnCoins(StoreFragment.AMOUNT_VERY_SMALL_COIN, preferences);
        else if (productId.equals(StoreFragment.SKU_SMALL_COIN)) CoinManager.earnCoins(StoreFragment.AMOUNT_SMALL_COIN, preferences);
        else if (productId.equals(StoreFragment.SKU_MEDIUM_COIN)) CoinManager.earnCoins(StoreFragment.AMOUNT_MEDIUM_COIN, preferences);
        else if (productId.equals(StoreFragment.SKU_BIG_COIN)) CoinManager.earnCoins(StoreFragment.AMOUNT_BIG_COIN, preferences);
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
    public void onBillingError(int i, Throwable throwable) {
        Log.e("IAB", "Got error(" + i + "):", throwable);
    }

    @Override
    public void onBillingInitialized() {
        Log.v("IAB", "Billing initialized.");
    }


    @Override
    public void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();

        super.onDestroy();
    }

    public void purchase(String sku) {
        if (billingProcessor.isInitialized())
            billingProcessor.purchase(sku);
        else
            ToastMaker.show(this, "در حال برقراری ارتباط با کافه بازار، کمی دیگر تلاش کنید.", Toast.LENGTH_SHORT);
    }

    OnPackagePurchasedListener mOnPackagePurchasedListener;

    public void setOnPackagePurchasedListener(OnPackagePurchasedListener onPackagePurchasedListener) {
        mOnPackagePurchasedListener = onPackagePurchasedListener;
    }

    public static interface OnPackagePurchasedListener {
        void packagePurchased(String sku);
    }
}

package ir.treeco.aftabe.New.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.BillingWrapper;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Adapter.AdItemAdapter;
import ir.treeco.aftabe.New.View.Custom.AutoScrollViewPager;
import ir.treeco.aftabe.New.CoinManager;
import ir.treeco.aftabe.New.Util.FontsHolder;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.New.Util.Tools;
import ir.treeco.aftabe.New.View.Custom.BackgroundDrawable;
import ir.treeco.aftabe.New.View.Fragment.GameFragmentNew;
import ir.treeco.aftabe.New.View.Fragment.MainFragment;
import ir.treeco.aftabe.New.View.Fragment.StoreFragment;
import ir.treeco.aftabe.R;

public class MainActivity extends FragmentActivity implements View.OnClickListener,
        BillingProcessor.IBillingHandler, CoinManager.CoinsChangedListener {
    private Context context;
    private Tools tools;
    private ImageView cheatButton;
    private ImageView logo;
    private boolean areCheatsVisible = false;
    private int currentLevel;
    private BillingProcessor billingProcessor;
    private TextView digits;
    private CoinManager coinManager;
    private OnPackagePurchasedListener mOnPackagePurchasedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

        digits = (TextView) findViewById(R.id.digits);
        coinManager = new CoinManager(this);
        tools = new Tools();

        cheatButton = (ImageView) findViewById(R.id.cheat_button);
        logo = (ImageView) findViewById(R.id.logo);

        cheatButton.setOnClickListener(this);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cheatButton.getLayoutParams();
        layoutParams.leftMargin = (int) (0.724 * MainApplication.lengthManager.getScreenWidth());
        layoutParams.topMargin = (int) (0.07 * MainApplication.lengthManager.getScreenWidth());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (fragmentManager.getBackStackEntryCount() != 0) throw new IllegalStateException();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();

        context = this;

        setUpCoinBox();
        setUpHeader();
        setOriginalBackgroundColor();

        billingProcessor = new BillingProcessor(this, this, BillingWrapper.Service.IRAN_APPS);
    }

    private void setUpCoinBox() {
        ImageView coinBox = (ImageView) findViewById(R.id.coin_box);

        int coinBoxWidth = MainApplication.lengthManager.getScreenWidth() * 9 / 20;
        int coinBoxHeight = MainApplication.lengthManager.getHeightWithFixedWidth(R.drawable.coin_box, coinBoxWidth);
        coinBox.setImageBitmap(ImageManager.loadImageFromResource(this, R.drawable.coin_box, coinBoxWidth, coinBoxHeight));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coinBox.getLayoutParams();
        layoutParams.topMargin = MainApplication.lengthManager.getScreenWidth() / 15;
        layoutParams.leftMargin = MainApplication.lengthManager.getScreenWidth() / 50;

        RelativeLayout.LayoutParams digitsLayoutParams = (RelativeLayout.LayoutParams) digits.getLayoutParams();
        digitsLayoutParams.topMargin = MainApplication.lengthManager.getScreenWidth() * 34 / 400;
        digitsLayoutParams.leftMargin = MainApplication.lengthManager.getScreenWidth() * 577 / 3600;
        digitsLayoutParams.width = MainApplication.lengthManager.getScreenWidth() / 5;

        digits.setTypeface(FontsHolder.getYekan(this));
        String number = "۸۸۸۸۸";
        digits.setText(number);

        coinBox.setOnClickListener(this);

        CoinManager coinManager = new CoinManager(getApplicationContext());
        coinManager.setCoinsChangedListener(this);
    }

    private void setUpHeader() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                MainApplication.lengthManager.getScreenWidth(),
                MainApplication.lengthManager.getHeaderHeight()
        ));

        logo.setImageBitmap(ImageManager.loadImageFromResource(
                this, R.drawable.header, MainApplication.lengthManager.getScreenWidth(),
                MainApplication.lengthManager.getScreenWidth() / 4
        ));
    }

    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#29CDB8"),
                Color.parseColor("#1FB8AA"),
                Color.parseColor("#0A8A8C")
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cheat_button:
                toggleCheatButton();
                break;

            case R.id.coin_box:
                if (StoreFragment.getIsUsed())
                    return;

                StoreFragment fragment = StoreFragment.getInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                break;
        }
    }

    public void setupCheatButton(int id) {
        cheatButton.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        areCheatsVisible = false;
        currentLevel = id;

        String cheatImagePath = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                + id + "_cheatBitmap.png";

        Picasso.with(context).load(cheatImagePath).into(cheatButton);
    }

    public void hideCheatButton() {
        cheatButton.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);
    }

    public void toggleCheatButton() {
        disableCheatButton(false);
        if (!areCheatsVisible) {
            String cheatImagePath = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                    + currentLevel + "_backBitmap.png";

            Picasso.with(context).load(cheatImagePath).into(cheatButton);
            areCheatsVisible = true;

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof GameFragmentNew)
                ((GameFragmentNew) fragment).showCheats();

        } else {
            String cheatImagePath = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                    + currentLevel + "_cheatBitmap.png";

            Picasso.with(context).load(cheatImagePath).into(cheatButton);
            areCheatsVisible = false;

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof GameFragmentNew)
                ((GameFragmentNew) fragment).hideCheats();
        }
    }

    public void disableCheatButton(boolean enable) {
        cheatButton.setClickable(enable);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingProcessor == null || !billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        if (productId.equals(StoreFragment.SKU_VERY_SMALL_COIN)) coinManager.earnCoins(StoreFragment.AMOUNT_VERY_SMALL_COIN);
        else if (productId.equals(StoreFragment.SKU_SMALL_COIN)) coinManager.earnCoins(StoreFragment.AMOUNT_SMALL_COIN);
        else if (productId.equals(StoreFragment.SKU_MEDIUM_COIN)) coinManager.earnCoins(StoreFragment.AMOUNT_MEDIUM_COIN);
        else if (productId.equals(StoreFragment.SKU_BIG_COIN)) coinManager.earnCoins(StoreFragment.AMOUNT_BIG_COIN);
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

    public interface OnPackagePurchasedListener {
        void packagePurchased(String sku);
    }


    public void purchase(String sku) {
        if (billingProcessor.isInitialized())
            billingProcessor.purchase(sku);
        else {
//            ToastMaker.show(this, "در حال برقراری ارتباط با کافه بازار، کمی دیگر تلاش کنید.", Toast.LENGTH_SHORT); //// TODO: 8/24/15
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();

        super.onDestroy();
    }
}

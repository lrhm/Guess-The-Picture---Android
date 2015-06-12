package ir.treeco.aftabe.New.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.BillingWrapper;
import com.anjlab.android.iab.v3.TransactionDetails;

import ir.treeco.aftabe.BackgroundDrawable;
import ir.treeco.aftabe.CoinManager;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Fragment.StoreFragment;
import ir.treeco.aftabe.View.Toast.ToastMaker;
import ir.treeco.aftabe.utils.Utils;

public class StoreActivity extends FragmentActivity implements BillingProcessor.IBillingHandler {

    OnPackagePurchasedListener mOnPackagePurchasedListener;
    private BillingProcessor billingProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        setOriginalBackgroundColor();

        billingProcessor = new BillingProcessor(this, this, BillingWrapper.Service.CAFE_BAZAAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    //region SetBackGroundDrawable
    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#F3C81D"),
                Color.parseColor("#F3C01E"),
                Color.parseColor("#F49C14")
        }));
    }
    //endregion

    @Override
    protected void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();

        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails transactionDetails) {
        SharedPreferences preferences = getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        if (productId.equals(StoreFragment.SKU_VERY_SMALL_COIN))
            CoinManager.earnCoins(StoreFragment.AMOUNT_VERY_SMALL_COIN, preferences);
        else if (productId.equals(StoreFragment.SKU_SMALL_COIN))
            CoinManager.earnCoins(StoreFragment.AMOUNT_SMALL_COIN, preferences);
        else if (productId.equals(StoreFragment.SKU_MEDIUM_COIN))
            CoinManager.earnCoins(StoreFragment.AMOUNT_MEDIUM_COIN, preferences);
        else if (productId.equals(StoreFragment.SKU_BIG_COIN))
            CoinManager.earnCoins(StoreFragment.AMOUNT_BIG_COIN, preferences);
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

    public void setOnPackagePurchasedListener(OnPackagePurchasedListener onPackagePurchasedListener) {
        mOnPackagePurchasedListener = onPackagePurchasedListener;
    }

    public static interface OnPackagePurchasedListener {
        void packagePurchased(String sku);
    }

    public void purchase(String sku) {
        if (billingProcessor.isInitialized())
            billingProcessor.purchase(sku);
        else
            ToastMaker.show(this, "در حال برقراری ارتباط با کافه بازار، کمی دیگر تلاش کنید.", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingProcessor == null || !billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

}

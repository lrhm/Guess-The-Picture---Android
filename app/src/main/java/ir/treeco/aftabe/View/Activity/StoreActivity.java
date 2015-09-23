package ir.treeco.aftabe.View.Activity;

import android.content.Intent;
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

import ir.treeco.aftabe.Adapter.CoinAdapter;
import ir.treeco.aftabe.View.Custom.BackgroundDrawable;
import ir.treeco.aftabe.View.Custom.ToastMaker;
import ir.treeco.aftabe.View.Fragment.StoreFragment;
import ir.treeco.aftabe.R;

public class StoreActivity extends FragmentActivity implements BillingProcessor.IBillingHandler {

    private OnPackagePurchasedListener mOnPackagePurchasedListener;
    private BillingProcessor billingProcessor;
    private CoinAdapter coinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        coinAdapter = new CoinAdapter(getApplicationContext());
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

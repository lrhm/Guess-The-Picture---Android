package ir.treeco.aftabe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import ir.treeco.aftabe.utils.*;

/**
 * Created by hamed on 9/2/14.
 */
public class StoreFragment extends Fragment implements BillingProcessor.IBillingHandler {
    public static final String SKU_VERY_SMALL_COIN = "very_small_coin";
    public static final String SKU_SMALL_COIN = "small_coin";
    public static final String SKU_MEDIUM_COIN = "medium_coin";
    public static final String SKU_BIG_COIN = "big_coin";
    public static final int AMOUNT_VERY_SMALL_COIN = 500;
    public static final int AMOUNT_SMALL_COIN = 1000;
    public static final int AMOUNT_MEDIUM_COIN = 2000;
    public static final int AMOUNT_BIG_COIN = 5000;
    static final int[] buttonIds = new int[] {
            R.id.very_small_coin,
            R.id.small_coin,
            R.id.medium_coin,
            R.id.big_coin
    };
    static final String[] SKUs = new String[] {
            SKU_VERY_SMALL_COIN,
            SKU_SMALL_COIN,
            SKU_MEDIUM_COIN,
            SKU_BIG_COIN
    };




    public static BillingProcessor billingProcessor;

    private static StoreFragment mInstance = null;
    final private String key = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC6dLXqc+NjfwuF3l0DB3Z1xYH96j94DH76M0zI5SA1I/FLj7Ei/wq1tY3yu6pHb+V6GU/BcucICdXtqRBsW8JPxdzcqO9KlpUY0Nk/KBehwt5YSb1bugf3IX4/arXpLrJG1gah4rPAfhsofR5ZHhrkBrkVuZ6DEaA9+jHK4WojpMnD5CNd3A7mrmFanZnNEFvTBYAQ36rru1voJbADNH397NZZYp55rIXRzY6B89sCAwEAAQ==";
    private View layout;

    public static StoreFragment getInstance() {
        if (mInstance == null)
            mInstance = new StoreFragment();
        return mInstance;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_store, container, false);

        Utils.setViewBackground(layout, new DialogDrawable(container.getContext()));

        {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
            layoutParams.leftMargin = layoutParams.rightMargin = layoutParams.topMargin = layoutParams.bottomMargin = LengthManager.getStoreDialogMargin();
        }

        {
            int padding = LengthManager.getStoreDialogPadding();
            layout.setPadding(padding, padding, padding, padding);
        }

        LoadingManager.startTask(null);
        billingProcessor = new BillingProcessor(getActivity(), key, this);

        for (int i = 0; i < SKUs.length; i++) {
            final int finalI = i;
            layout.findViewById(buttonIds[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    billingProcessor.purchase(SKUs[finalI]);
                }
            });
        }

        ImageView shopTitle = (ImageView) layout.findViewById(R.id.shop_title);
        Bitmap shopTitleBitmap = ImageManager.loadImageFromResource(shopTitle.getContext(), R.drawable.shoptitle, LengthManager.getShopTitleWidth(),  -1);
        shopTitle.setImageBitmap(shopTitleBitmap);
        Utils.resizeView(shopTitle, shopTitleBitmap.getWidth(), shopTitleBitmap.getHeight());
        ((LinearLayout.LayoutParams) shopTitle.getLayoutParams()).bottomMargin = LengthManager.getShopTitleBottomMargin();

        return layout;
    }

    private void setupItemsList(View view) {
        ImageView background = (ImageView) view.findViewById(R.id.store_items);
        Bitmap backgroundBitmap = ImageManager.loadImageFromResource(view.getContext(), R.drawable.store_items, LengthManager.getStoreItemsInnerWidth(), -1);
        Utils.resizeView(background, backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
        background.setImageBitmap(backgroundBitmap);

        ImageView extraBackground = (ImageView) view.findViewById(R.id.store_extra_item);
        Bitmap extraBackgroundBitmap = ImageManager.loadImageFromResource(view.getContext(), R.drawable.extra_item, LengthManager.getStoreItemsInnerWidth(), -1);
        Utils.resizeView(extraBackground, extraBackgroundBitmap.getWidth(), extraBackgroundBitmap.getHeight());
        extraBackground.setImageBitmap(extraBackgroundBitmap);


        float[] titleTops = new float[] {0.03f, 0.355f, 0.68f};
        int[] revenues = new int[] {500, 2000, 5000};
        int[] prices = new int[] {500, 1500, 3000};
        for (int i = 0; i < titleTops.length; i++) {
            final LinearLayout item = (LinearLayout) view.findViewById(buttonIds[i]);
            String persianPrice = Utils.numeralStringToPersianDigits("" + prices[i]);
            setupItem(item, (int) (titleTops[i] * backgroundBitmap.getHeight()), "فقط " + persianPrice + " تومان", i, revenues[i]);
        }

        setupItem((LinearLayout) view.findViewById(R.id.review_cafebazaar), (int) (extraBackgroundBitmap.getHeight() * 0.15), "نظر دادن در بازار", 3, 300);
        view.findViewById(R.id.review_cafebazaar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastMaker.show(view.getContext(), "سلام به آفتابه ی خالی ما خوش اومدی عزیزم :)ییثیصحثینحثصخنحلصث ث بثص بصث ب صث بثص ب ثصلثق لق ثل ثقل ز ر س رصث ر  ذلذ ق ف", Toast.LENGTH_LONG);
            }
        });
    }

    private void customizeTextView(TextView textView, String label) {
        textView.setText(label);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getStoreItemFontSize());
        textView.setTextColor(Color.WHITE);
        textView.setShadowLayer(1, 2, 2, Color.BLACK);
        textView.setTypeface(FontsHolder.getHoma(textView.getContext()));
    }

    private void setupItem(LinearLayout item, int topMargin, String label, int index, int revenueAmount) {
        final FrameLayout.LayoutParams itemLayoutParams = (FrameLayout.LayoutParams) item.getLayoutParams();
        itemLayoutParams.topMargin = topMargin;
        itemLayoutParams.height = LengthManager.getStoreItemHeight();
        itemLayoutParams.width = LengthManager.getStoreItemWidth();
        itemLayoutParams.leftMargin = itemLayoutParams.rightMargin = LengthManager.getStoreItemHorizontalMargin();

        TextView title = (TextView) item.getChildAt(index % 2);
        TextView revenue = (TextView) item.getChildAt(1 - index % 2);

        customizeTextView(title, label);
        title.getLayoutParams().width = LengthManager.getStoreItemTitleWidth();
        //title.setBackgroundColor(Color.BLUE);

        customizeTextView(revenue, Utils.numeralStringToPersianDigits("" + revenueAmount));
        revenue.getLayoutParams().width = LengthManager.getStoreItemRevenueWidth();
        //revenue.setBackgroundColor(Color.RED);
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadingManager.endTask();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails transactionDetails) {
        SharedPreferences preferences = getActivity().getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        if (productId.equals(SKU_VERY_SMALL_COIN)) CoinManager.earnCoins(AMOUNT_VERY_SMALL_COIN, preferences);
        if (productId.equals(SKU_SMALL_COIN)) CoinManager.earnCoins(AMOUNT_SMALL_COIN, preferences);
        if (productId.equals(SKU_MEDIUM_COIN)) CoinManager.earnCoins(AMOUNT_MEDIUM_COIN, preferences);
        if (productId.equals(SKU_BIG_COIN)) CoinManager.earnCoins(AMOUNT_BIG_COIN, preferences);
        billingProcessor.consumePurchase(productId);
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
    }

    @Override
    public void onBillingInitialized() {
        setupItemsList(layout);
        LoadingManager.endTask();
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();
        super.onDestroy();
    }
}

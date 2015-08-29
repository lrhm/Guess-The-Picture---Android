package ir.treeco.aftabe.New.View.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Adapter.DBAdapter;
import ir.treeco.aftabe.New.CoinManager;
import ir.treeco.aftabe.New.Util.FontsHolder;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.New.Util.Tools;
import ir.treeco.aftabe.New.View.Activity.MainActivity;
import ir.treeco.aftabe.New.View.Custom.DialogDrawable;
import ir.treeco.aftabe.R;

/**
 * Created by behdad on 8/23/15.
 */

public class StoreFragment extends Fragment {
    private Tools tools;
    private DBAdapter db;
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
    private static final String CAFEBAZAAR_REVIEWED = "cafebazaar_reviewed";

    private static StoreFragment mInstance = null;
    private View layout;
    private static boolean isUsed;

    public static StoreFragment getInstance() {
        if (mInstance == null) {
            mInstance = new StoreFragment();
        }
        return mInstance;
    }

    public StoreFragment() {
        tools = new Tools();
        db = DBAdapter.getInstance(getActivity());
    }

    public static boolean getIsUsed() {
        return isUsed;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        isUsed = true;

        layout = inflater.inflate(R.layout.fragment_store, container, false);

        int margin = MainApplication.lengthManager.getStoreDialogMargin();
        layout.setPadding(margin, margin, margin, margin);
        View dialog = layout.findViewById(R.id.dialog);
        tools.setViewBackground(dialog, new DialogDrawable(container.getContext()));

        int padding = MainApplication.lengthManager.getStoreDialogPadding();
        dialog.setPadding(padding, padding, padding, padding);

        for (int i = 0; i < SKUs.length; i++) {
            final int finalI = i;
            layout.findViewById(buttonIds[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).purchase(SKUs[finalI]);
                }
            });
        }

        final View reviewBazaar = layout.findViewById(R.id.review_cafebazaar);
        if (db.getCoinsReviewed()) {
            reviewBazaar.setVisibility(View.GONE);
        } else {
            reviewBazaar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.updateReviewed(true);

                    CoinManager coinManager = new CoinManager(getActivity());
                    coinManager.earnCoins(30000);

                    Intent browserIntent = new Intent(Intent.ACTION_EDIT, Uri.parse("http://cafebazaar.ir/app/ir.treeco.aftabe/?l=fa"));
                    startActivity(browserIntent);

                    reviewBazaar.setVisibility(View.GONE);
                }
            });
        }

        ImageView shopTitle = (ImageView) layout.findViewById(R.id.shop_title);
        Bitmap shopTitleBitmap = ImageManager.loadImageFromResource(shopTitle.getContext(), R.drawable.shoptitle, MainApplication.lengthManager.getShopTitleWidth(), -1);

        shopTitle.setImageBitmap(shopTitleBitmap);
        tools.resizeView(shopTitle, shopTitleBitmap.getWidth(), shopTitleBitmap.getHeight());
        ((ViewGroup.MarginLayoutParams) shopTitle.getLayoutParams()).bottomMargin = MainApplication.lengthManager.getShopTitleBottomMargin();

        setupItemsList();

        return layout;
    }

    private void setupItemsList() {
        int[] revenues = new int[] {500, 1000, 2000, 15000, 300};
        int[] prices = new int[] {450, 800, 1500, 5000, -1};

        LinearLayout itemsList = (LinearLayout) layout.findViewById(R.id.items_list);

        FrameLayout[] items = new FrameLayout[5];
        for (int i = 0; i < 5; i++)
            items[i] = (FrameLayout) itemsList.getChildAt(i);

        for (int i = 0; i < items.length; i++) {
            String persianPrice = "فقط " + tools.numeralStringToPersianDigits("" + prices[i]) + " تومان";
            if (i == 4)
                persianPrice = "نظر در بازار";
            setupItem(items[i],  persianPrice, revenues[i], i % 2 == 1);
        }
    }

    private void customizeTextView(TextView textView, String label) {
        textView.setText(label);

        textView.setTypeface(FontsHolder.getHoma(textView.getContext()));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainApplication.lengthManager.getStoreItemFontSize());
        textView.setTextColor(Color.WHITE);

        textView.setShadowLayer(1, 2, 2, Color.BLACK);
    }

    private void setupItem(FrameLayout item, String label, int revenueAmount, boolean reversed) {
        final ViewGroup.LayoutParams itemLayoutParams = item.getLayoutParams();
        itemLayoutParams.height = MainApplication.lengthManager.getStoreItemHeight();
        itemLayoutParams.width = MainApplication.lengthManager.getStoreItemWidth();

        ImageView itemBackground = (ImageView) item.findViewById(R.id.item_background);
        itemBackground.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), reversed? R.drawable.single_button_green: R.drawable.single_button_red, MainApplication.lengthManager.getStoreItemWidth(), MainApplication.lengthManager.getStoreItemHeight()));

        TextView title = (TextView) item.findViewById(R.id.label);
        customizeTextView(title, label);

        TextView revenue = (TextView) item.findViewById(R.id.price);
        customizeTextView(revenue, tools.numeralStringToPersianDigits("" + revenueAmount));

        if (reversed) {
            LinearLayout textViews = (LinearLayout) item.findViewById(R.id.text_views);
            tools.reverseLinearLayout(textViews);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isUsed = false;
    }
}
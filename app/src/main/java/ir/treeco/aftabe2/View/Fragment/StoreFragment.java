package ir.treeco.aftabe2.View.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import ir.tapsell.tapsellvideosdk.developer.DeveloperInterface;
import ir.treeco.aftabe2.Adapter.CoinAdapter;
import ir.treeco.aftabe2.Adapter.DBAdapter;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.DialogDrawable;

public class StoreFragment extends Fragment {
    private Tools tools;
    private DBAdapter db;
    private ImageManager imageManager;
    private LengthManager lengthManager;
    public static final String SKU_VERY_SMALL_COIN = "very_small_coin";
    public static final String SKU_SMALL_COIN = "small_coin";
    public static final String SKU_MEDIUM_COIN = "medium_coin";
    public static final String SKU_BIG_COIN = "big_coin";
    public static final int AMOUNT_VERY_SMALL_COIN = 500;
    public static final int AMOUNT_SMALL_COIN = 2000;
    public static final int AMOUNT_MEDIUM_COIN = 4000;
    public static final int AMOUNT_BIG_COIN = 12500;
    static final int[] buttonIds = new int[]{
            R.id.very_small_coin,
            R.id.small_coin,
            R.id.medium_coin,
            R.id.big_coin
    };
    static final String[] SKUs = new String[]{
            SKU_VERY_SMALL_COIN,
            SKU_SMALL_COIN,
            SKU_MEDIUM_COIN,
            SKU_BIG_COIN
    };


    private View layout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_store, container, false);

        tools = new Tools(getActivity());
        db = DBAdapter.getInstance(getActivity());
        imageManager = ((MainApplication) getActivity().getApplicationContext()).getImageManager();
        lengthManager = ((MainApplication) getActivity().getApplicationContext()).getLengthManager();

        int margin = lengthManager.getStoreDialogMargin();
        layout.setPadding(margin, margin, margin, margin);
        View dialog = layout.findViewById(R.id.dialog);
        tools.setViewBackground(dialog, new DialogDrawable(getActivity()));

        int padding = lengthManager.getStoreDialogPadding();
        dialog.setPadding(padding, padding, padding, padding);

        final int[] prices = new int[]{1000, 3000, 4000, 10000, -1, -1};

        for (int i = 0; i < SKUs.length; i++) {
            final int finalI = i;
            layout.findViewById(buttonIds[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).purchase(SKUs[finalI], prices[finalI]);
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

                    CoinAdapter coinAdapter = new CoinAdapter(getActivity(), getActivity());

                    Intent browserIntent = new Intent(Intent.ACTION_EDIT, Uri.parse("http://cafebazaar.ir/app/ir.treeco.aftabe/?l=fa"));
                    startActivity(browserIntent);

                    reviewBazaar.setVisibility(View.GONE);
                }
            });
        }

        View tapsell = layout.findViewById(R.id.tapsell_free_coin);
        tapsell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                CoinAdapter coinAdapter = new CoinAdapter(getActivity(), getActivity());


                DeveloperInterface.getInstance(getActivity()).showNewVideo(getActivity(),
                        DeveloperInterface.TAPSELL_DIRECT_ADD_REQUEST_CODE,
                        DeveloperInterface.DEFAULT_MIN_AWARD,
                        DeveloperInterface.VideoPlay_TYPE_NON_SKIPPABLE);
            }
        });

        ImageView shopTitle = (ImageView) layout.findViewById(R.id.shop_title);
        Bitmap shopTitleBitmap = imageManager.loadImageFromResource(R.drawable.shoptitle, lengthManager.getShopTitleWidth(), -1);

        shopTitle.setImageBitmap(shopTitleBitmap);
        tools.resizeView(shopTitle, shopTitleBitmap.getWidth(), shopTitleBitmap.getHeight());
        ((ViewGroup.MarginLayoutParams) shopTitle.getLayoutParams()).bottomMargin = lengthManager.getShopTitleBottomMargin();

        setupItemsList();

        return layout;
    }

    private void setupItemsList() {
        int[] revenues = new int[]{500, 2000, 4000, 12500, 300, 20};
        int[] prices = new int[]{1000, 3000, 4000, 10000, -1, -1};

        LinearLayout itemsList = (LinearLayout) layout.findViewById(R.id.items_list);

        FrameLayout[] items = new FrameLayout[6];
        for (int i = 0; i < 6; i++)
            items[i] = (FrameLayout) itemsList.getChildAt(i);

        for (int i = 0; i < items.length; i++) {
            String persianPrice = "فقط " + tools.numeralStringToPersianDigits("" + prices[i]) + " تومان";
            int j = i;
            if (i == 4)
                persianPrice = "نظر در بازار";
            if (i == 5) {
                persianPrice = "تبلیغ ببین سکه ببر";
                if (db.getCoinsReviewed()) {
                    j = i - 1;
                }
            }
            setupItem(items[i], persianPrice, revenues[i], i % 2 == 1);
        }
    }

    private void customizeTextView(TextView textView, String label) {
        textView.setText(label);

        textView.setTypeface(FontsHolder.getSansMedium(textView.getContext()));

        UiUtil.setTextViewSize(textView, lengthManager.getStoreItemHeight(), 0.275f);
        textView.setTextColor(Color.WHITE);

        textView.setShadowLayer(1, 2, 2, Color.BLACK);
    }

    private void setupItem(FrameLayout item, String label, int revenueAmount, boolean reversed) {
        final ViewGroup.LayoutParams itemLayoutParams = item.getLayoutParams();
        itemLayoutParams.height = lengthManager.getStoreItemHeight();
        itemLayoutParams.width = lengthManager.getStoreItemWidth();

        ImageView itemBackground = (ImageView) item.findViewById(R.id.item_background);
        itemBackground.setImageBitmap(imageManager.loadImageFromResource(reversed ? R.drawable.single_button_green : R.drawable.single_button_red, lengthManager.getStoreItemWidth(), lengthManager.getStoreItemHeight()));

        TextView title = (TextView) item.findViewById(R.id.label);
        customizeTextView(title, label);

        TextView revenue = (TextView) item.findViewById(R.id.price);
        customizeTextView(revenue, tools.numeralStringToPersianDigits("" + revenueAmount));

//        if (reversed) {
//            LinearLayout textViews = (LinearLayout) item.findViewById(R.id.text_views);
//            tools.reverseLinearLayout(textViews);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).setStore(false);
    }
}
package ir.treeco.aftabe2.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import ir.treeco.aftabe2.Util.Logger;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe2.Adapter.Cache.PackageSolvedCache;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Object.PackageObject;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Custom.DialogDrawable;


public class PackagePurchaseDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "PackagePurchaseDialog";
    Context context;
    RelativeLayout mDataContainer;
    Tools tools;
    PackageObject packageObject;
    SizeConverter packageConverter;
    ImageManager imageManager;
    private View.OnClickListener yesClick;

    public PackagePurchaseDialog(Context context, PackageObject packageObject) {
        super(context);
        this.context = context;
        tools = new Tools(context);
        this.packageObject = packageObject;


    }

    public PackagePurchaseDialog setYesClick(View.OnClickListener yesClick) {
        this.yesClick = yesClick;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_package_purchase_view);


        packageConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.30f, 300, 300);

        mDataContainer = (RelativeLayout) findViewById(R.id.user_data_container);
        RelativeLayout.LayoutParams layoutParams = new
                RelativeLayout.LayoutParams((int) (0.8 * SizeManager.getScreenWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = (int) (packageConverter.mHeight * 0.45f);
        mDataContainer.setLayoutParams(layoutParams);
        tools.setViewBackground(mDataContainer, new DialogDrawable(getContext()));

        imageManager = ((MainApplication) getContext().getApplicationContext()).getImageManager();

        UiUtil.setWidth(findViewById(R.id.dialog_match_request_text_containers), (int) (SizeManager.getScreenWidth() * 0.8));

        setUpPackageImage();

        UiUtil.setTopMargin(findViewById(R.id.dialog_package_purchase_1), (int) (packageConverter.mHeight * 0.6f));


        String title = packageObject.getName();

        String desc = packageObject.getSku();

        User myUser = Tools.getCachedUser(context);

        int intPrice = ((myUser != null && myUser.isPackagePurchased(packageObject.getId()))) ? 0 : packageObject.getPrice();
        intPrice = PackageSolvedCache.getInstance().isPackagePurchased(packageObject.getId()) ? 0 : intPrice;

        String price = String.format("%s %s %s", "ناقابل", Tools.numeralStringToPersianDigits(intPrice + ""), "سکه");

        float packageSize = packageObject.getPackageSize() / (float) (1000000);

        String packageS = Tools.numeralStringToPersianDigits(packageSize + "");
        try {
            packageS = packageS.substring(0, 4);
        } catch (Exception e) {
        }
        Logger.d(TAG, "package size " + packageObject.getPackageSize() / 1000 + " " + packageS);


        String size = String.format("%s %s %s", "حجم:", packageS, "مگابایت");


        setUpTextView(R.id.dialog_package_purchase_1, title, true, 0.4f);

        setUpTextView(R.id.dialog_package_purchase_2, desc, false, 0.3f);

        setUpTextView(R.id.dialog_package_purchase_3, price, false, 0.3f);

        setUpTextView(R.id.dialog_package_purchase_4, size, false, 0.3f);

        setUpBuyButtons(intPrice);


    }

    void setUpBuyButtons(int price) {

        ImageView packagePurchaseButton = (ImageView) findViewById(R.id.dialog_package_purchase_image);
        TextView packagePurcahseText = (TextView) findViewById(R.id.dialog_package_purchase_text);

        SizeConverter buttonSize = SizeConverter.SizeConvertorFromWidth(packageConverter.mWidth * 1.1f, 776, 245);
        packagePurchaseButton.setImageBitmap(imageManager.loadImageFromResourceNoCache(
                R.drawable.buybutton, buttonSize.mWidth, buttonSize.mHeight, ImageManager.ScalingLogic.FIT));

        UiUtil.setTextViewSize(packagePurcahseText, (int) (SizeManager.getScreenHeight() * 0.1), 0.3f);
        packagePurcahseText.setTypeface(FontsHolder.getSansBold(context));
        packagePurcahseText.setTextColor(Color.WHITE);

        String text = "دانلود";
        if (price != 0)
            text = "خرید و دانلود";
        packagePurcahseText.setText(text);

        packagePurchaseButton.setOnClickListener(this);

        UiUtil.setTopMargin(findViewById(R.id.dialog_package_purchase_buy_container), (int) (SizeManager.getScreenHeight() * 0.02));

        UiUtil.setBottomMargin(findViewById(R.id.dialog_package_purchase_buy_container), (int) (SizeManager.getScreenHeight() * 0.05));

    }

    void setUpPackageImage() {


        ImageView packageImageView = (ImageView) findViewById(R.id.dialog_package_purchase_package_image);

        UiUtil.setHeight(packageImageView, packageConverter.mHeight);
        UiUtil.setWidth(packageImageView, packageConverter.mWidth);

        String imagePath;
        imagePath = "file://" + context.getFilesDir().getPath() + "/package_" + packageObject.getId() + "_" + "front" + ".png";

        if (packageObject.isThereOffer() && packageObject.getShownPrice(context) != 0)
            imagePath = "file://" + packageObject.getOfferImagePathInSD(context);


        User myUser = Tools.getCachedUser(context);
        Picasso.with(context).load(imagePath).fit().into(packageImageView);

    }


    private void setUpTextView(int id, String text, boolean bold, float size) {

        TextView titleTextView = (TextView) findViewById(id);

        titleTextView.setTypeface(FontsHolder.getFont(context, bold ? FontsHolder.SANS_BOLD : FontsHolder.SANS_MEDIUM));
        titleTextView.setText(text);

        UiUtil.setTextViewSize(titleTextView, (int) (SizeManager.getScreenHeight() * 0.1), size);
        if (!bold)
            UiUtil.setTopMargin(titleTextView, (int) (SizeManager.getScreenHeight() * 0.02));

//        int leftMargin = (int) (SizeManager.getScreenWidth() * 0.8 - UiUtil.getTextViewWidth(titleTextView));
//        UiUtil.setLeftMargin(titleTextView, leftMargin / 2);

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {

        if (yesClick != null)
            yesClick.onClick(v);

        dismiss();

    }
}


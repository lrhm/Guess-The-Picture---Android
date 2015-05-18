package ir.treeco.aftabe;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ir.treeco.aftabe.View.Fragment.PackageFragment;
import ir.treeco.aftabe.View.Toast.ToastMaker;
import ir.treeco.aftabe.packages.DownloadProgressListener;
import ir.treeco.aftabe.packages.MetaPackage;
import ir.treeco.aftabe.packages.NotificationProgressListener;
import ir.treeco.aftabe.packages.Package;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.utils.FontsHolder;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.LoadingManager;
import ir.treeco.aftabe.utils.TaskStartedListener;
import ir.treeco.aftabe.utils.Utils;

/**
 * Created by hamed on 8/12/14.
 */
public class PackageListImplicitAdapter {
    private int filter;
    private LinearLayout mPackagePurchaseDialog;

    abstract class PackageInfoListener implements View.OnTouchListener, View.OnClickListener {
        float x;
        float y;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            x = motionEvent.getX();
            y = motionEvent.getY();
            return false;
        }
    }

    class PackageInfoTag {
        ImageView frontCard;
        ImageView backCard;
    }

    private IntroActivity activity;
    private PackageManager pManager;
    private MetaPackage[] packages;

    public final static int NEW_TAB = 2, LOCAL_TAB = 1, HOT_TAB = 0;

    public void flip(int i, View view) {
        if (!itemsData[i].enabled)
            return;
        view.clearAnimation();
        view.startAnimation(itemsData[i].toMiddle);
    }

    static class ItemData {
        boolean flipped = false;
        boolean enabled = true;
        Animation toMiddle;
        Animation fromMiddle;
    }

    ItemData[] itemsData;

    public PackageListImplicitAdapter(IntroActivity activity, PackageManager pManager) {
        this.activity = activity;
        this.pManager = pManager;
        setFilter(-1);
    }

    void setFilter(int filter) {
        this.filter = filter;
        switch (filter) {
            case NEW_TAB:
                packages = pManager.getNewPackages();
                break;
            case LOCAL_TAB:
                packages = pManager.getLocalPackages();
                break;
            case HOT_TAB:
                packages = pManager.getHotPackages();
                break;
            default:
                packages = new MetaPackage[0];
        }
        itemsData = new ItemData[packages.length];
        for (int i = 0; i < itemsData.length; i++) {
            itemsData[i] = new ItemData();
            itemsData[i].toMiddle = AnimationUtils.loadAnimation(activity, R.anim.to_middle);
            itemsData[i].fromMiddle = AnimationUtils.loadAnimation(activity, R.anim.from_middle);
        }
        notifyDataSetChanged();
    }

    public int getFilter() {
        return filter;
    }

    public void notifyDataSetChanged() {
        for (DataSetObserver observer : observers)
            observer.onChanged();
    }

    List<DataSetObserver> observers = new ArrayList<DataSetObserver>();

    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        observers.add(dataSetObserver);
    }

    public int getCount() {
        return itemsData.length;
    }

    public View getView(final int i, View packageInfo, final ViewGroup viewGroup) {
        if (packageInfo == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            packageInfo = inflater.inflate(R.layout.view_package_info, null);

            final PackageInfoTag tag = new PackageInfoTag();
            tag.frontCard = (ImageView) packageInfo.findViewById(R.id.front_card);
            tag.backCard = (ImageView) packageInfo.findViewById(R.id.back_card);

            packageInfo.setTag(tag);
        }

        final int mySize = LengthManager.getPackageIconSize();

        final PackageInfoTag tag = (PackageInfoTag) packageInfo.getTag();
        final View finalPackageInfo = packageInfo;
        final MetaPackage aPackage = packages[i];
        final ItemData aItemData = itemsData[i];

        aItemData.toMiddle.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                aItemData.enabled = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finalPackageInfo.clearAnimation();
                finalPackageInfo.startAnimation(aItemData.fromMiddle);
                Utils.toggleVisibility(tag.frontCard);
                Utils.toggleVisibility(tag.backCard);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        aItemData.fromMiddle.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                aItemData.enabled = true;
                aItemData.flipped = !aItemData.flipped;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        {
            final InputStream front;
            final InputStream back;
            try {
                front = aPackage.getFront();
                back = aPackage.getBack();
            } catch (FileNotFoundException e) {
                throw new RuntimeException();
            }

            tag.frontCard.setImageBitmap(null);
            tag.frontCard.clearAnimation();
            tag.backCard.setImageBitmap(null);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    {
                        final Bitmap frontBitmap = ImageManager.loadImageFromInputStream(front, mySize, mySize);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final DownloadingDrawable frontDrawable = new DownloadingDrawable(frontBitmap);
                                tag.frontCard.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tag.frontCard.setImageDrawable(frontDrawable);
                                        Animation fadeIn = new AlphaAnimation(0, 1);
                                        fadeIn.setInterpolator(new DecelerateInterpolator());
                                        fadeIn.setDuration(200);
                                        tag.frontCard.clearAnimation();
                                        tag.frontCard.startAnimation(fadeIn);
                                    }
                                });
                            }
                        });
                    }
                    {
                        final Bitmap backBitmap = ImageManager.loadImageFromInputStream(back, mySize, mySize);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tag.backCard.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tag.backCard.setImageBitmap(backBitmap);
                                    }
                                });
                            }
                        });
                    }
                }
            }).start();
        }

        packageInfo.setLayoutParams(new LinearLayout.LayoutParams(mySize, mySize));

        tag.frontCard.setVisibility(aItemData.flipped ? View.GONE : View.VISIBLE);
        tag.backCard.setVisibility(aItemData.flipped ? View.VISIBLE : View.GONE);

        PackageInfoListener packageInfoListener = new PackageInfoListener() {
            @Override
            public void onClick(View view) {
                if (aItemData.flipped || x < mySize / 3 && y < mySize / 3) {
                    PackageListImplicitAdapter.this.flip(i, view);
                    return;
                }

                if (aPackage.getIsDownloading()) {
                    DownloadCancelAlert dialog = new DownloadCancelAlert(aPackage);
                    dialog.show(activity.getSupportFragmentManager(), "CancelDownload");
                    return;
                }

                final NotificationProgressListener notificationProgressListener = new NotificationProgressListener(aPackage.getContext(), aPackage);

                final DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
                    DownloadingDrawable drawable = (DownloadingDrawable) tag.frontCard.getDrawable();
                    int last = 0;

                    @Override
                    public void update(final int progressInPercentage) {
                        if (last != progressInPercentage) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawable.setPercentage(progressInPercentage);
                                }
                            });
                        }
                        last = progressInPercentage;
                    }

                    @Override
                    public void success() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                drawable.setPercentage(100);
                                setFilter(filter);
                            }
                        });
                    }

                    @Override
                    public void failure() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                drawable.setPercentage(100);
                                ToastMaker.show(activity, "دانلود بسته با مشکل روبرو شد :(", Toast.LENGTH_LONG);
                            }
                        });
                    }
                };

                final SharedPreferences preferences = activity.getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);

                switch (aPackage.getState()) {
                    case REMOTE: {
                        if (aPackage.isPurchased()) {
                            aPackage.becomeLocal(new DownloadProgressListener[]{
                                    notificationProgressListener,
                                    downloadProgressListener
                            });
                            break;
                        }
                        createPackagePurchaseDialog(packages[i],
                                // Purchase From Bazaar
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i("Purchase", "from bazaaar " + aPackage.getSku());
                                        activity.setOnPackagePurchasedListener(new IntroActivity.OnPackagePurchasedListener() {
                                            @Override
                                            public void packagePurchased(String sku) {
                                                aPackage.becomeLocal(new DownloadProgressListener[]{
                                                        notificationProgressListener,
                                                        downloadProgressListener
                                                });
                                                hidePackagePurchaseDialog();
                                            }
                                        });
                                        activity.purchase(aPackage.getSku());
                                    }
                                },
                                // Purchase with coins
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (CoinManager.getCoinsCount(preferences) < aPackage.getCoinCost()) {
                                            ToastMaker.show(activity, activity.getString(R.string.not_enought_coins), Toast.LENGTH_SHORT);
                                        } else {
                                            CoinManager.spendCoins(aPackage.getCoinCost(), preferences);
                                            aPackage.becomeLocal(new DownloadProgressListener[]{
                                                    notificationProgressListener,
                                                    downloadProgressListener
                                            });
                                            hidePackagePurchaseDialog();
                                        }
                                    }
                                },
                                // Cancel Purchase
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        hidePackagePurchaseDialog();
                                    }
                                });
                        break;
                    }
                    case LOCAL: {
                        LoadingManager.startTask(new TaskStartedListener() {
                            @Override
                            public void taskStarted() {
                                PackageFragment fragment = PackageFragment.newInstance(new Package(aPackage));
                                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, fragment, "package_" + aPackage.getName());
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });
                        break;
                    }
                }
            }
        };

        packageInfo.setOnClickListener(packageInfoListener);
        packageInfo.setOnTouchListener(packageInfoListener);

        return packageInfo;
    }

    enum TextType {
        TITLE,
        DESCRIPTION,
        ITEM_TEXT
    }

    private void customizeTextView(TextView textView, String label, TextType textType) {
        textView.setText(label);
        textView.setTypeface(FontsHolder.getHoma(textView.getContext()));

        switch (textType) {
            case TITLE: {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getPackagePurchaseTitleSize());
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(1, 2, 2, Color.BLACK);
                break;
            }
            case DESCRIPTION: {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getPackagePurchaseDescriptionSize());
                textView.setTextColor(Color.GRAY);
                break;
            }
            case ITEM_TEXT: {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getPackagePurchaseItemTextSize());
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(1, 2, 2, Color.BLACK);
                break;
            }
        }
    }

    private void createPackagePurchaseDialog(MetaPackage metaPackage, View.OnClickListener fromBazaar, View.OnClickListener byCoin, View.OnClickListener cancelPurchase) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPackagePurchaseDialog = (LinearLayout) inflater.inflate(R.layout.view_package_purchase, null);
        activity.pushToViewStack(mPackagePurchaseDialog, true);

        LinearLayout dialog = (LinearLayout) mPackagePurchaseDialog.findViewById(R.id.dialog);

        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialog.getLayoutParams();
            layoutParams.leftMargin = layoutParams.rightMargin = layoutParams.topMargin = layoutParams.bottomMargin = LengthManager.getStoreDialogMargin();
        }

        {
            int padding = LengthManager.getLevelFinishedDialogPadding();
            dialog.setPadding(padding, padding, padding, padding);
        }

        {
            TextView title = (TextView) dialog.findViewById(R.id.title);
            customizeTextView(title, (String) title.getText(), TextType.TITLE);
        }

        {
            TextView description = (TextView) dialog.findViewById(R.id.description);
            customizeTextView(description, (String) description.getText(), TextType.DESCRIPTION);
        }

        {
            View purchaseFromBazaar = dialog.findViewById(R.id.purchase_from_bazaar);
            purchaseFromBazaar.setOnClickListener(fromBazaar);
            setupStoreItem(purchaseFromBazaar, "خرید از بازار", Utils.numeralStringToPersianDigits("" + metaPackage.getTomanCost()), R.drawable.single_button_green, true);
        }

        {
            View purchaseByCoin = dialog.findViewById(R.id.purchase_by_coin);
            purchaseByCoin.setOnClickListener(byCoin);
            setupStoreItem(purchaseByCoin, "خرید با سکه", Utils.numeralStringToPersianDigits("" + metaPackage.getCoinCost()), R.drawable.single_button_red, false);
        }

        Utils.setViewBackground(dialog, new DialogDrawable(activity));
    }

    void hidePackagePurchaseDialog() {
        if (mPackagePurchaseDialog == null)
            return;
        activity.popFromViewStack(mPackagePurchaseDialog);
        mPackagePurchaseDialog = null;
    }

    private void setupStoreItem(View storeItem, String label, String price, int resourceId, boolean reversed) {
        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) storeItem.getLayoutParams();
            layoutParams.width = LengthManager.getPackagePurchaseItemWidth();
            layoutParams.height = LengthManager.getPackagePurchaseItemHeight();
            layoutParams.gravity = Gravity.CENTER;
        }

        if (reversed) {
            LinearLayout textViews = (LinearLayout) storeItem.findViewById(R.id.text_views);
            Utils.reverseLinearLayout(textViews);
        }

        {
            TextView itemLabel = (TextView) storeItem.findViewById(R.id.label);
            customizeTextView(itemLabel, label, TextType.ITEM_TEXT);
        }

        {
            TextView itemPrice = (TextView) storeItem.findViewById(R.id.price);
            customizeTextView(itemPrice, price, TextType.ITEM_TEXT);
        }

        {
            ImageView background = (ImageView) storeItem.findViewById(R.id.item_background);
            background.setImageBitmap(ImageManager.loadImageFromResource(activity, resourceId, LengthManager.getPackagePurchaseItemWidth(), -1));
        }
    }
}

